
#MaduraUtils#
	

The MaduraUtils project is a catch-all project for various miscellaneous features required by other Madura projects (or anyone else) but which aren't big enough to justify their own project.

 * Locking: this provides a factory and template for using `java.util.concurrent.locks.Lock` implementations as well as two of those implementations. We re-interpret the `Lock` interface here because the original intention for that interface seems to be more about locking memory objects such as lists and queues, across threads. This re-interpretation is about pessimistic locks of abstract items that*might*be locked across multiple threads and multiple JVMs, depending on the implementation.
 * Parser is a set of parsing tools which can be extended to support a specific grammar fairly simply.
 * Schemaparser is a reader of XSD files which creates an object structure which can be easily queried for class names and fields etc.
