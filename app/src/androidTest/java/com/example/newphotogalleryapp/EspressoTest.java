package com.example.newphotogalleryapp;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static androidx.test.espresso.intent.Intents.intending;

public class EspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    //tests if date search works
    public void TestTag() {
        String d1 = "20191020";
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.startDate)).perform(typeText(d1));
        onView(withId(R.id.endDate)).perform(typeText(d1));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(d1))));
        for (int i = 1; i < 9; i++) {
            onView(withId(R.id.button_right)).perform(click());
        }
    }

    @Test
    public void TestSearchIntentOpens() {
        Intents.init();
        onView(withId(R.id.button_search)).perform(click());
        intended(hasComponent(SearchActivity.class.getName()));

    }

    //testing use of single word tag
    @Test
    public void WordTag() {
        String w1 = "health";

        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.Tags)).perform(typeText(w1));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(w1))));

    }

    //testing use of 2 word tags
    @Test
    public void WordTag2() {
        String w2 = "math";

        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.Tags)).perform(typeText(w2));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(w2))));

    }
    //testing use of 2 word tags
    @Test
    public void bigtagtest() {
        String w5 = "VERYVERYVERYVERYVERYVERYBIGTAGTEST1111111111111111111";

        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.Tags)).perform(typeText(w5));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(w5))));

    }
    @Test
    public void WordTagMultiple() {
        String w3 = "all";

        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.Tags)).perform(typeText(w3));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());

        for (int i = 1; i < 4; i++) {
            onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(w3))));
            onView(withId(R.id.button_right)).perform(click());
        }

        onView(withId(R.id.timestamp_textview)).check(matches(withText(containsString(w3))));

    }

    @Test
    public void NotWordTag() {
        String w4 = "notInTags";

        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.Tags)).perform(typeText(w4));
        closeSoftKeyboard();
        onView(withId(R.id.button_search)).perform(click());
        onView(withId(R.id.timestamp_textview)).check(matches(not(withText(containsString(w4)))));

    }
}