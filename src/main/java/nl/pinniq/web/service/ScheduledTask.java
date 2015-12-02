/*
Copyright 2015 pinniq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package nl.pinniq.web.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import nl.pinniq.web.backing.ProcessState;

@Component
public class ScheduledTask {
	
	private Set<ProcessRunnable> work = new HashSet<ProcessRunnable>();
	
	@Autowired
	@Qualifier("getAsyncExecutor") //no mistakes which bean to get
	private ThreadPoolTaskExecutor executor;
	
	@Autowired
	private SimpMessagingTemplate template;
		
	@Scheduled(fixedRate = 1000)
    public void run() {
		for (ProcessRunnable workerProcess : work) {
			//only start the ones not started
			if (workerProcess.getState()==ProcessState.STATE_NOT_STARTED) { 
				executor.execute(workerProcess);
			}
		}
	}
	
	/**
	 * Adds a new worker process to the work package
	 */
	public void addWork() {
		ProcessRunnable process = new AsyncProcessRunnableImpl();
		process.setTemplate(template);
		work.add(process);
	}

	/**
	 * Retrieves the work package
	 * @return
	 */
	public Set<ProcessRunnable> getWork() {
		return this.work;
	}

	/**
	 * Removes a worker process from the work package
	 * @param id of the worker process
	 */
	public void removeWork(String id) {
		Predicate<ProcessRunnable> filter = new Predicate<ProcessRunnable>() {
			@Override
			public boolean test(ProcessRunnable t) {
				return id.equalsIgnoreCase(t.getProcessId()); 
			}
		};
		work.remove(work.removeIf(filter));
	}
	
}
