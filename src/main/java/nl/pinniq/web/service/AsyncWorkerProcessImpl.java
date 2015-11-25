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

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import nl.pinniq.web.backing.ProcessState;
import nl.pinniq.web.backing.ProcessStatus;

public class AsyncWorkerProcessImpl implements WorkerProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SimpMessagingTemplate template;
	
	private static final Logger LOGGER = Logger.getLogger(AsyncWorkerProcessImpl.class.getCanonicalName());

	private AtomicInteger status = new AtomicInteger(0);
	private String processId;
	private Date dateTime;
	private Integer state;
	
	public AsyncWorkerProcessImpl() {
		this.dateTime = new Date();
		this.processId = UUID.randomUUID().toString();
		this.state = ProcessState.STATE_NOT_STARTED;
	}

	/**
	 * Run a sample process, updates status in between process steps to subscribed clients
	 */
	@Override
	public void run()  {
		this.state = ProcessState.STATE_BUSY;
		updateStatus(0, "");
		updateStatus(10, "Initializing");
		try {
			Thread.sleep(5000);
			updateStatus(10, "Done");
			Thread.sleep(5000);
			updateStatus(20, "Done");
			Thread.sleep(5000);
			updateStatus(20, "Done");
			Thread.sleep(5000);
			updateStatus(20, "Done");
			Thread.sleep(5000);
			updateStatus(20, "Finished");
		} catch (InterruptedException e) {
			LOGGER.error("Thread interrupt error", e);
			updateStatus(0, "");
		}
		this.state = ProcessState.STATE_FINISHED;
	}
	
	/**
	 * Updates status/progress to registered clients.
	 * Javascript client code for subscribing to serverside messages: 
	 * stompClient.subscribe('/topic/progress', callBackFuntion); 
	 * 
	 * @param updateBy
	 * @param message
	 */
	private void updateStatus(int updateBy, String message) {
		if (0==updateBy) {
			status.set(0);
		}
		ProcessStatus processStatus = new ProcessStatus();
		processStatus.setMessage(message);
		processStatus.setPrecentComplete(status.addAndGet(updateBy));
		processStatus.setId(this.processId);
		template.convertAndSend("/topic/progress", processStatus);
	}

	public String getProcessId() {
		return this.processId;
	}

	public Date getDateTime() {
		return this.dateTime;
	}

	public Integer getState() {
		return this.state;
	}

	@Override
	public void setTemplate(SimpMessagingTemplate template) {
		this.template = template;
	}
	
}
