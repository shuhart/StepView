StepView
======================

A simple animated step view for Android. Backward and forward animations is supported.

Usage
-----

1. Add jcenter() to repositories block in your gradle file.
2. Add `compile 'com.shuhart.stepview:stepview:1.2.1'` to your dependencies.
3. Add `StepView` into your layouts or view hierarchy.

Supported animations:

Name| Preview
-------- | ---
`ANIMATION_LINE`| ![animation_line](/images/animation_line.gif)
`ANIMATION_CIRCLE`| ![animation_circle](/images/animation_circle.gif)
`ANIMATION_ALL`| ![animation_all](/images/animation_all.gif)
`ANIMATION_NONE`| ![animation_none](/images/animation_none.gif)

In ANIMATION_CIRCLE and ANIMATION_NONE examples the line color remains the same. You can achieve this by specifying:
``` app:doneStepLineColor="@color/stepview_line_next" ```

Usage:

Specify steps with xml attribute:
```xml
	app:steps="@array/steps"
```

```java
	stepView.setSteps(List<String> steps);
```

Or Specify numbers of steps so that only circles with step number are shown:

```xml
	app:stepsNumber="4"
```

```java
	stepView.setStepsNumber(4);
```

<img src="/images/no_text.png"/>


Styling:

```xml
<com.shuhart.stepview.StepView
	android:id="@+id/step_view"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:padding="16dp"
	app:selectedCircleColor="@color/colorAccent"
	app:selectedTextColor="@color/colorAccent"
	app:stepLineWidth="1dp"
	app:stepPadding="4dp"
    app:nextTextColor="@color/colorAccent"
	app:nextStepLineColor="@color/colorAccent"
	app:doneCircleColor="@color/colorAccent"
	app:doneStepLineColor="@color/colorAccent"
	app:doneCircleRadius="12dp"
	app:selectedCircleRadius="12dp"
	app:selectedStepNumberColor="@color/colorPrimary"
	app:stepViewStyle="@style/StepView"
	app:doneStepMarkColor="@color/colorPrimary"
	app:stepNumberTextSize="12sp"
	app:animationType="Line"
    app:typeface="@font/roboto_italic"/>
```

or instantiate and setup it in runtime with handy state builder:

```java
    stepView.getState()
            .selectedTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            .animationType(StepView.ANIMATION_CIRCLE)
            .selectedCircleColor(ContextCompat.getColor(this, R.color.colorAccent))
            .selectedCircleRadius(getResources().getDimensionPixelSize(R.dimen.dp14))
            .selectedStepNumberColor(ContextCompat.getColor(this, R.color.colorPrimary))
            // You should specify only stepsNumber or steps array of strings
            // In case you specify both steps array are chosen.
            .steps(new ArrayList<String>() {{
                add("First step");
                add("Second step");
                add("Third step");
            }})
            // You should specify only steps number or steps array of strings
            // In case you specify both steps array are chosen.
            .stepsNumber(4)
            .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
            .stepLineWidth(getResources().getDimensionPixelSize(R.dimen.dp1))
            .textSize(getResources().getDimensionPixelSize(R.dimen.sp14))
            .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.sp16))
            .typeface(ResourcesCompat.getFont(context, R.font.roboto_italic))
            // other state methods are equal to the corresponding xml attributes
            .commit();
```

If you want to mark last step with a done mark:

```java
	stepView.done(true);
```
If you want to allow going back after that, you should unmark the done state:

```java
	stepView.done(false)
```

If you want a custom typeface you should add font files to the resource folder "font" and reference any in xml layout.
Alternatively you can specify typeface using the state builder in your code. Look into the sample for additional details on that.

License
=======

    Copyright 2017 Bogdan Kornev.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
