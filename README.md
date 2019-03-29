## Description
POC of `ThreadLocal` type of `@Bean`, propagated throughout Reactive Flow

Takes APP_NAME value from HTTP header and puts it into `ReactiveThreadLocal` variable.

## Usage
Start app and curl to `/test` with APP_NAME header

  