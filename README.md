# LTI

Exposes endpoints that make our content consumable via [Learning Tools Interoperability](https://www.imsglobal.org/activity/learning-tools-interoperability).

## Versions and features compatibility

### LTI 1.1

- [`basic-lti-launch-request`](https://www.imsglobal.org/specs/ltiv1p1/implementation-guide): videos and collections exposed through the basic resource launch.


### LTI 1.3

- [`LtiResourceLinkRequest`](http://www.imsglobal.org/spec/lti/v1p3): video, collection and search UIs exposed through the resource link message,
- [`LtiDeepLinkingRequest`](http://www.imsglobal.org/spec/lti-dl/v2p0): the LTI Advantage Deep Linking flow.

## Development

### Local environment

Use the provided script to setup [ktLint](https://ktlint.github.io) and download local development configuration:

```
$ ./setup
```

Running the app locally with the `local` Spring profile activated:

```
$ ./gradlew bootRunLocal
```

#### Selecting a dev server

You can switch between the LTI views.

Go to `webpack-config/webpack.dev.js` and change the devServer index file name.

### Testing

Running tests:

```
$ ./gradlew test
```

### Shipping changes

There is a convenience [`ship`](./ship) script that will check for upstream changes, test your app and do a Git push.
