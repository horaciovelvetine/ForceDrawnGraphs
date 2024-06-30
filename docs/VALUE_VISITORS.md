# Wikidata Value-Visitor: The Visitor Pattern

The [Java Wikidata Toolkit](https://github.com/Wikidata/Wikidata-Toolkit) implements a few classes ending with a Visitor keyword, this is a reference to a  design pattern first published in [Design Patterns - Elements of Reusable Object-Software](https://en.wikipedia.org/wiki/Design_Patterns). The book contains C++ code examples, discussions of Object Oriented solutions, design patterns which scale well at enterprise, is a tremendous resouce and can be found anywhere. 

Discussion on the `Visitor` pattern begins on pg. 331 - if you want to get **THE** comprehensive reference that is **THE** place to start. 

Ignorantly encountering this pattern is enormously frustrating, ironically due to the intended behavior. These sort of esoteric structures are pretty tough to read to about to an understanding, but exist as a means of organizing operations as a class to act on a diverse, or even distinct set of objects which share a behavior not easily (or best) implemented as an interface.

Did that help? No?

## A Contextual Example: 

Inside of the Wikidata toolkit the Visitor pattern emerges as the `SnakVisitor` & `ValueVisitor`'s, with the most straightforward example being the `ValueVisitor`. Reading the [docs](https://wikidata.github.io/Wikidata-Toolkit/org/wikidata/wdtk/datamodel/interfaces/ValueVisitor.html) this interface asks that its implementors provide a - `visit()` method for 7 of the returnable value types. Now imagine if you, in calling using API, wanted to make a query based request and know there will be (value...) results, but you don't know their types. In order to make those results more meaningful, you'd likely end up type narrowing out something verbose like this:  

```java
public void whatKindaResultsAreThese(List<Values> results){
  // etc... with handling for each of the 7 result types
  for (Value value : results) { 
    if (value instanceof TimeValue) {
      handleTimeValue((TimeValue) value);
    } else if (value instanceof StringValue) {
      handleStrigValue((StringValue) value)
    } else {
      // etc... with handling for each of the 7 result types
    }
  }
}
```

This isn't the worst thing - but with interfaces can be handled easily behind the scenes without the verbosity. A single class using this interface handles all this type narrowing for you, and allows defining custom behavior for each implementing (result value) type. 

You can see this implemented in practice inside the `UnknownSnakVisitor.java` class, with only 3 potential result types important, the custom handling to format Times differently from Strings from Entity ID's is all organized in a single place.