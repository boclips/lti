# LTI Launch Request Endpoint

Exposes endpoints that make our content consumable via LTI protocol.

Currently focused around [LTI 1.1](https://www.imsglobal.org/specs/ltiv1p1/implementation-guide), with a plan to enable [LTI 1.3](https://www.imsglobal.org/spec/lti/v1p3/) as a next step. 

## Current assumptions

Given this is our first LTI endpoint and we have a single LMS that will be integrating with us, the plan is to expose a simplest working implementation quickly and validate it against that LMS.

Support for an arbitrary number of Tool Consumers and more elaborate content will be added afterwards.

## Development

Running tests:

```
$ ./gradlew test
```
