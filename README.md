# tent-text-java

A library to expand shortcuts in a [Tent](https://tent.io)-friendly way.

It is a fork of [twitter-text-java](https://github.com/twitter/twitter-text-java).

## Installation

Java 6 is required. Maven is required to build the library.

tent-text-java is currently unpublished, so run the following to get started:

* git clone git@github.com:mwanji/tent-text-java.git
* mvn install

You can then add the dependency to your POM.

## Usage

The main class you'll interact with is `com.moandjiezana.tent.text.Autolink`.

### Autolink methods

* autoLinkMentionsAndLists(String): converts mentions (eg. ^mwanji) within a block of text to links (eg. `<a href="https://mwanji.tent.is">mwanji</a>`)
* autoLinkHashtags(String): converts hashtags (eg. #tentdev) within a block of text to links (eg. `<a href="https://skate.io/search?q=%23tentdev">#tentdev</a>`)
* autoLinkURLs(String): converts plain URLs within a block a text into HTML links.
* autoLink(String): combines all of the above

### Autolink configuration

Setters are used to customise Autolink's behaviour.

* mentionClass: CSS class applied to mentions (default: "username")
* mentionLinker: a SAM-type that converts a plain mention into a URL (default produces https://<mention>.tent.is)
* hashtagUrlBase: the root of the URL used for hashtags (default: "https://skate.io/search?q=")
* hashtagClass: CSS class applied to hashtags (default: "hashtag")
* noFollow: if true, `rel="nofollow"` is added to each link (default: true)