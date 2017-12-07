# Reproduce issue with lagom service streaming

The initial need was to implement wich take a Source as input stream and reply to it with a Strict output when source is fully consumed 
The same thing as in this mailing list discussion https://groups.google.com/forum/#!topic/lagom-framework/Sd-pUP1HRhk/discussion
 
As discussed on the gitter channel I moved the implemenation to a bidirectionnal streamed service but the response source seems always truncated and returned empty

This project is to narrow down the issue on isolation

To reproduce the issue run 
```
$ sbt test
```