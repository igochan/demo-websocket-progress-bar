# asyncworkerdemo

Demo showing progress (using bootstrap progress bar) of server side async processes
using WebSockets.

<ul>
<li>Java version: JDK 8</li>
<li>Spring version: 4.1.7.RELEASE</li>
<li>Bootstrap version: 3.3.5</li>
<li>Bootstrap table version: 1.9.1</li>
</ul>

Use `mvn clean install` to bake war file and deploy into your favorite application server.
Browse to `http://<appserver_domain>:<appserver_port>/asyncworkerdemo/`

Push 'Add work' to add a process to the table (and to server side work load). Progress bar shows progress of the server process. Updates from server to client are sent using STOMP messaging (stomp.js) over WebSockets and Spring `WebSocketMessageBroker`

Push 'Add work' to add more and see the async capabilities.