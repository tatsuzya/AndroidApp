package com.example.newphotogalleryapp;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

public class EspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    //tests if date search works
    public void Test1() {
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.startDate)).perform(typeText("20191015"));
        onView(withId(R.id.endDate)).perform(typeText("20191015"));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        // onView(withId(R.id.timestamp_textview)).check(matches(withText("20191015")));
        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString("20191015"))));

    }
}