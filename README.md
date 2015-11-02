# Worker - clojure implementation

This is a tiny spike to try creating a worker for the JSON decompress/write 
application in clojure. 

**Requires** 

[leiningen](http://leiningen.org/)

### Usage

During development, it's easiest to: 

1. Auth with AWS (environment variables are easiest, but will use creds file too)
2. `lein run some-s3-bucket some-s3-key.json.gz some-dynamodb-table-to-write-to` 

### Known problems: 

Because operations is operating on various threads, they tend not to surface to stdout errors
while operating under a `future`. This is less than ideal and I'll look for a way to fix it.

### Why Clojure? 

**Language Maturity**

This was originally a golang project. However, Golang's native dependency management is [problematic](http://jbeckwith.com/2015/05/29/dependency-management-go/)
and requires third party libraries to be stable (at project start a breaking change by AWS's SDK
had silently broken the project). AWS makes it clear their stuff is [still under active development](https://github.com/aws/aws-sdk-go#caution).

By contrast, clojure's AWS SDK in this instances is a reflection-wrapped version of the 
Java SDK. It's more-or-less feature-complete and versioned by leiningen from Maven.  

**Library support**

Clojure is a hosted languge, acting as a library on top of Java. Interop calls to 
Java classes, methods, primitives and support for interfaces and the like 
are [implemented for trivial use](http://clojure.org/java_interop).

Using Leiningen to install dependencies, you have: 
    
    - [Clojars.org](https://clojars.org/)
    - [Maven repositories](http://mvnrepository.com/)

Of course, libaries in clojure are preferable, but if they're not obviously available
(like the gzip stream reader in this project), it's trivial to fall-back on the Java 
implementation (see the use of `java.util.zip.GZIPInputStream.` in `src/worker.get.clj`)

**Transforming JSON**
Golang's statically typed C-like semantics (static typing, Structs) 
[aren't ideal](https://github.com/brightsparc/brightsparc-go/blob/master/api.go#L161) 
for JSON transforms. 

Clojure is a dynamically typed language with a 1:1 equivalence between 
native datastructures and JSON. 

JSON: 

    {"foo": ["bar", "baz"]}

Clojure maps and vectors: 

    {:foo ["bar" "baz"]}

**Concurrency support**

Go blocks - it's concurrency primitives which work like buffered internal queues, 
were [copied](http://clojure.github.io/core.async/) by clojure some time ago. However, 
having immutability by default in a language makes any concurrency support significantly easier.
Additionally clojure's advanced language constructs like 
[software-transactional-memory](http://sw1nn.com/blog/2012/04/11/clojure-stm-what-why-how/)
do allow for modification of values between threads in a safe manner. 

### Todo: 

- Testing
- Actors for threading
- retry behaviour for database writes
