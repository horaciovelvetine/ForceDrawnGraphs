# Fuzzy Matching Strings 

Inside the `FuzzyStringMatch.java` class a rudimentary implementation of a fuzzy string matching utility leverages two simple approaches to calculate the raltive matchyness of two strings. Practically in this application, this is being used to help match Properties (which define the nature of an edge), to a possibly already existing Vertex Entity (ex. a Career is something which someone may have, and at the same time would have a matching ItemDoc(Vertex)).

## Approaches

1) Most simply, and the one you wouldn't even have to google to think of is simply searching for Vertices where the label contains the Property label substring. These are combined with the second approach as a pre-filter to reduce the number of comparisons that need to be made.
  
2) The second approach utilizes the Levenshtein distance algorithm which compares the number of edits needed to be made to transform one string into another. This method seemed the most (personally) implementable, and easily understandable for the String matching task at hand. 
   - The Levenshtein Distance Algorithm is a no-brainer choice given that only two short strings (labels) should be compared in this application.
   - Below references provide a step by step implementation of the algorithm, and a conceptual walkthrough of different implementation complexities, and their trade-offs, I highly suggest spinning up a test app and coding along - this clarifies the otherwise abstract dual array implementation stopped on here.
   - Transpositions are ignored because they are more common in human entered data as repeated charcters -or- accidentally swapped characters (ex. "teh" instead of "the"). The source of info is Wikidata, and the general quality and dedication of their team, this implementation operates a bit faster. 
   - Two seperate versions of the algo are implemented in the class, firstly a dynamic approach which closely matches the formal definition of the algorithm `LevenshteinStringMatch.computeLevenshteinDistance(Str1, Str2)` and secondly a more application targeted version `LevenshteinStringMatch.computeLevApproxWithThreshold(Str1, Str2, Thresh)`. The second approach weeds out the less likely matches by using the already existing threshold value and exiting early if the distance exceeds it, and only storing the current and previous row of the matrix to save on space (allowing for less strict matching and at the same time faster computation).

## References
- [Fuzzy Search Algorithm for Approximate String Matching by Baeldung](https://www.baeldung.com/cs/fuzzy-search-algorithm)
- [Levenshtein Distance by Baeldung](https://www.baeldung.com/cs/levenshtein-distance-computation)