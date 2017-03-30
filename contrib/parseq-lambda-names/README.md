Parseq Lambda Names
==========================

This project aims to provide more meaningful default descriptions for Parseq tasks. By default parseq uses 
Lambda class name as default description which is bad because: Lambda class names are not deterministic and 
it is difficult to locate Lambda class in source code.

Using ASM, this project tries to locate where lambda expression is defined in source code and also infer some 
details about its execution like function call within lambda expression with number of arguments.


How to use
==========================

The shaded jar of parseq-lambda-names should be present on classpath along with parseq jar in order to analyze
generated Lambda classes once when Lambda is executed for first time. If parseq-lambda-names jar is not present
on classpath, then parseq will behave as usual i.e. uses Lambda class name as task description.

Limitations
==========================

As this project uses ASM to analyze generated Lambda bytecode, it can potentially break between minor JVM versions. 
Currently its tested for jvm versions: 1.8.0_5, 1.8.0_40, 1.8.0_72

