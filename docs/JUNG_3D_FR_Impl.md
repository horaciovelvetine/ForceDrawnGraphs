# JUNG - Implementing a 3D Fruchterman-Reingold


## Duplicate Caches?

Inside the calcPositions method, called as the last calculation in the step() function the first few lines contain a bit of a conundrum. Both layout classes `AbstractLayout & FRLayout`, implement a `LoadingCache` as `locations & frVertexData` respectively to store the location data for it's vertices which appears to be duplication at first glance. The `FRVertexData` class extends the `Point2D` class adding a few simple methods for relevant math helpers, so why store this information twice, accident? This duplication is actually a crafty Java trick hiding in matching clothing. 

```java
    protected synchronized void calcPositions(V v) {
        FRVertexData fvd = getFRData(v); // references the FRLayouts - frVertexData
        if(fvd == null) return;
        Point2D xyd = apply(v); // references the AbstraceLayouts - locations
        double deltaLength = Math.max(EPSILON, fvd.norm());

        double newXDisp = fvd.getX() / deltaLength
                * Math.min(deltaLength, temperature);

        if (Double.isNaN(newXDisp)) {
        	throw new IllegalArgumentException(
                "Unexpected mathematical result in FRLayout:calcPositions [xdisp]"); }

        double newYDisp = fvd.getY() / deltaLength
                * Math.min(deltaLength, temperature);
        xyd.setLocation(xyd.getX()+newXDisp, xyd.getY()+newYDisp);

        double borderWidth = getSize().getWidth() / 50.0;
        double newXPos = xyd.getX();
        if (newXPos < borderWidth) {
            newXPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newXPos > (getSize().getWidth() - borderWidth)) {
            newXPos = getSize().getWidth() - borderWidth - Math.random()
                    * borderWidth * 2.0;
        }

        double newYPos = xyd.getY();
        if (newYPos < borderWidth) {
            newYPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newYPos > (getSize().getHeight() - borderWidth)) {
            newYPos = getSize().getHeight() - borderWidth
                    - Math.random() * borderWidth * 2.0;
        }

        xyd.setLocation(newXPos, newYPos); // but, update only the locations point data?
    }
``` 
It appears as though data in the `frVertexData` cache would be set on init, then never updated for the positions. However, this is not the case both cache's are referencing the same instance of `Point2D.FRVertexData`, and so the `setLocations()` call works to modify the same value. Why? To provide concurrent access to this information across both classes continuously. This creative solution has several strong positives: isolates the minimal behavior extension of Point2D in the layout itself, ensures consistent asynchronus access across the algorithim to a single source of truth. 