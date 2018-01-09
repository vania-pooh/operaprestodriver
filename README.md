OperaPrestoDriver
=================

OperaPrestoDriver is a vendor-supported
[WebDriver](http://dvcs.w3.org/hg/webdriver/raw-file/tip/webdriver-spec.html)
implementation developed by [Opera](http://opera.com/) and volunteers
that enable programmatic automation of Presto-based Opera
products (i.e. v12 and older). It is a part of the
[Selenium](http://code.google.com/p/selenium) project.

__Note that OperaPrestoDriver is only compatible with Presto-based Opera browsers
up until v12.1*. For Blink-based Operas (v15 and onwards), refer to [the
OperaChromiumDriver project](https://github.com/operasoftware/operachromiumdriver).__

The rest of documentation was intentionally removed.

## Building

```
$ mvn clean compile
```

## Testing

Opera Presto binary is required. Not sure whether it works.

```
$ mvn clean test
```