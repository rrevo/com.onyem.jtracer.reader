
Onyem 
======

## Introduction

[Onyem JTracer](http://www.onyem.com) is a Java development tool that helps you understand your code by creating beautiful visualizations of the program execution flow. Create the next generation of interactive sequence diagrams automatically without changing code.


Reader
======

[Reader](http://www.onyem.com/help/reader.html) is an Eclipse RCP based application for creating the actual visualization of the traced files.

This project is the source for the next version of the project that has been re-architected to add a local database which should allow addition of new features.

## Usage

The project uses maven as the build tool.

### Building

Build target

    mvn install

### Run

The final Eclipse RCP artifacts are present in the com.onyem.jtracer.reader.ui.rcp sub-project. To run the linux 64 rcp invoke

    com.onyem.jtracer.reader.ui.rcp/target/products/com.onyem.jtracer.reader.ui.rcp.product/linux/gtk/x86_64/reader

For other platforms, execute the appropriate binary


## Feedback

Please provide feedback on [Github](https://github.com/rrevo) or [Onyem](http://www.onyem.com/contact.html)

