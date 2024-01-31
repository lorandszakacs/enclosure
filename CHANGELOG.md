# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

# unreleased

### Scala versions:

- `2.12.18`, JVM and JS
- `2.13.12`, JVM and JS
- `3.3.1`, JVM and JS

### internals:
- bump sbt-scalajs to `1.15.0`
- bump sbt-scalafmt to `2.5.2`
- bump scalafmt to `3.7.17`
- bump sbt to `1.9.8`
- bump munit to `1.0.0-M10`
- bump sbt-typelevel tp `0.6.5`

# 1.0.0

Stable, LTS release. There's little reason for the library to change API/semantics any time soon. Scala steward maintenance for the forseable future.

### Scala versions:

- `2.12.15`, JVM and JS
- `2.13.8`, JVM and JS
- `3.1.3`, JVM and JS

### internals:

- bump scalafmt to `3.5.8`, use less vertical alignment
- bump sbt to `1.7.1`
- bump munit to `1.0.0-M1`
- replace `sbt-spiewak` with `sbt-typelevel`. Also remove all sbt-plugins that are included in the latter.

# 0.1.2

- add Scala `3.0.0` :party:, JVM and JS. Drop support for Scala `3.0.0-RC2` and `3.0.0-RC3`.

### internals:

- bump sbt to `1.5.2`
- bump `munit` to `0.7.26`
- temporarily use `sbt-dotty` `0.5.5` to allow publishing to Scala 3.

# 0.1.1

- add Scala `3.0.0-RC3`, JVM and JS. Drop support for Scala `3.0.0-RC1`

### internals:

- use scalafmt `3.0.0-RC1`, finally, formatting for Scala 3 code!
- bump `munit` to `0.7.25`

# 0.1.0

First release!

### Scala versions:

- `2.12`, JVM and JS
- `2.13`, JVM and JS
- `3.0.0-RC1`, JVM and JS
- `3.0.0-RC2`, JVM and JS

### Dependencies

- munit `0.7.23` - testing only
