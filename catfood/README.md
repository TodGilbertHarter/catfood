# catfood
Build a CMS usable for my own projects which is relatively simple, includes only needed features, and is relatively easy to manage and maintain.

## Basic Needs
1. Java - Sorry folks, there's lots of techs out there for building webapps but one endures. I know this language, its libraries, its tools, and it can do what I want. I can't predict the fate of other technologies, but the JVM apparently will endure. 
2. Embedded Database - Need a database, and need one I don't have to manage separately if I don't need to. Something running in the JVM is thus ideal.
3. Free - This is a hobby, I'm not paying for tools, so it all really needs to be OSS.
4. Robust Front End - Javascript is a mess and client-side frameworks come and go. We need something flexible, yet relatively lightweight. Still, it needs to be able to handle rendering a relatively modern-looking UI efficiently.

## Solution
Vert.x 3.x forms the core of our solution. CatFood code can thus be written in a variety of JVM languages, including Javascript. This is a modern high-performance toolkit which allows us to build a cutting-edge reactor-pattern based application which can scale up, or run as a single deployment with minimal configuration required.

OrientDB provides the embedded database. This is a modern document-oriented/graph DB which has modern features, is flexible, can be run in a client-server mode and scale horizontally if desired, but also works fine as a simple in-process JVM embedded service. It understands SQL as well as some other standard query languages and seems to do what we want. We could use some embedded SQL RDBMS, but why not store documents in a document database?

There are no perfect choices for Javascript front-end frameworks. In fact Javascript/HTML/CSS as a platform sucks rocks in Hell. I've chosen to utilize React. It's fast, it is relatively lightweight, and JSX templating can be leveraged to make some fairly clean UI code. It is also possible to pre-render React templates on the server side (yes, even in Nashorn, the JVM Javascript, which is about 90% as fast as Node.js, so we aren't suffering much there). We should be able to couple this with some form of data manager like Redux, but we'll see about that, one thing at a time.

# license
See the included license file, this application is licensed GPLv3 or newer. 