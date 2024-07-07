# JUNG - Implementing a 3D Fruchterman-Reingold


## Duplicate Caches?

The `AbstractLayout` method is inherited by each of the layout algorithm and uses a `LoadingCache<V, Point2D>` to store location data for each of the vertices. However, as each solution is looked at a little more closely it begins to appear that each algorithm initializes it's own secondary cache, and in the case of the `FRLayouts` even opting to use the exact same `LoadingCache<V, Point2D>` under the `frVertexData` label. 

Inside the various methods used to build a layout, you can even see them being seperately referenced in the same equation... 

```java
    protected synchronized void calcPositions(V v) { //FRLayout excerpt..
        FRVertexData fvd = getFRData(v); // references FRLayouts - frVertexData
        if(fvd == null) return;
        Point2D xyd = apply(v); // references AbstractLayouts - locations
        double deltaLength = Math.max(EPSILON, fvd.norm());

        double newXDisp = //calc... apply cumulative delta change using xyd
        double newYDisp = //calc... same as newXDisp

        double borderWidth = // define acceptable border limits
        double newXPos = // check if pos violates border, correct and dither edge positions
        double newYPos = //calc... sams as newXPos
        
        xyd.setLocation(newXPos, newYPos); // but, update only the locations point data?
    }
``` 

Iterable solutions rely on a second store to persist the offset calculations accumulated through each iterative loop, being reset each iteration back to the origin. A bit more poking around in a very related layout ``KKLayout` has a distance matrix (`private double[][] dm;`) to handle the exact same need of persiting data in a second place. 

The `step()` method consists of three main function calls while calculating a full iteration: `calcRepulsion(V v) => calcAttraction(E e) => calcPosition(V v)` with the logical flow following that order. Resetting each vertice's offset position to (0,0) happens in `calcRepulsion(V v)` ending when that offset is used and applied inside of `calcPosition(V v)`


