# README

a loop view

# Preview

![](http://7xitw5.com1.z0.glb.clouddn.com/2017-10-27%2016_48_40.gif)

# Use

```kotlin
val adapter = CustomLoopViewAdapter(this, data)
loopView.setAdapter(adapter)
adapter.notifyChanged()
loopView.setAnimationDuration(1000)
loopView.startFlipping()
```
        
        
# Methods
 fun startFlipping() 
 
 fun setFlipInterval(milliseconds: Int)
 
 fun setAnimationDuration(duration: Long)
 
 fun setIndicator(showPositionDrawable: GradientDrawable, noShowPositionDrawable: GradientDrawable)
