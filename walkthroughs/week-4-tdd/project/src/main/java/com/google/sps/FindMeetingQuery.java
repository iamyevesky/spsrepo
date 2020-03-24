// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<Event> NO_EVENTS = Collections.emptySet();
    Collection<String> NO_ATTENDEES = Collections.emptySet();
    long LENGTH_OF_DAY = 24*60;

    if (events == null){
        throw new IllegalArgumentException("Events parameter should not be null");
    }
    else if (request == null){
        throw new IllegalArgumentException("Request parameter should not be null");
    }

    //If the length of the meeting is longer than a day, possibilities are zero
    if(request.getDuration() >= LENGTH_OF_DAY){
        return Arrays.asList();
    }
    //If no events are slated then everyone can attend event. This whole day is available
    else if (events == NO_EVENTS){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    //If there are events but the meeting has no attendees them it can take place at any time
    else if(request.getAttendees() == NO_ATTENDEES){
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    

    ArrayList<TimeRange> output = new ArrayList<>();
    output.add(TimeRange.WHOLE_DAY);
    //For each event:
    //1. Get the attendees for the event
    //2. If any of the attendees are set to attend the meeting request, move on to step 4. 
    //   ...  Move to step 3 if otherwise.
    //3. If no attendees are in the request, continue.
    //4. Create a new array "newOutput". Then, check for each TimeRange object in output:
    //     a. Check if the object contains the event's time range.
    //         i. If yes, split the time range object into two.
    //            ...  For example if event starts at 8:00am and duration is an hour,
    //            ...  Split TimeRange.WHOLE_DAY into two TimeRange objects:
    //            ...  TimeRange(0, 60*8) and TimeRange(9*60, 24*59)
    //         ii. Add these TimeRange objects to the new array.
    //         iii. Continue, since no other TimeRange object in output will overlap with event's TimeRange
    //     b. If not check if time two TimeRange objects overlap.
    //          i. If yes, check the periods they overlap.
    //          ii. Remove the time of overlap between the two time objects in a new TimeRange object
    //          iii. Add the result to the new array
    //     c. Assign newOutput to output
    
    //After the run for each TimeRange object in the new array:
    //1. Check if the duration is equal or more than the duration of the period
    //2. If yes, remove item.
    //3. Return list

    Iterator<Event> eventsIterator = events.iterator(); 
    while (eventsIterator.hasNext()){
      Event event = eventsIterator.next();
      Set<String> attendees = event.getAttendees();
      boolean containsAttendee = false;
      for (String attendee : attendees){
          if (request.getAttendees().contains(attendee)){
            containsAttendee = true;
            break;
          }
      }

      if (!containsAttendee){
          continue;
      }
      ArrayList<TimeRange> newOutput = new ArrayList<>();
      for(TimeRange object : output){
        if (object.contains(event.getWhen())){
          int startMinuteFirstSplit = object.start();
          int endMinuteFirstSplit = event.getWhen().start();
          int startMinuteSecondSplit = event.getWhen().end();
          int endMinuteSecondSplit = object.end();
          newOutput.add(TimeRange.fromStartEnd(startMinuteFirstSplit, endMinuteFirstSplit, false));
          newOutput.add(TimeRange.fromStartEnd(startMinuteSecondSplit, endMinuteSecondSplit, false));
        }
        else if(object.overlaps(event.getWhen())){
          if (event.getWhen().start() <= object.start()){
            int startMinute = event.getWhen().end();
            int endMinute = object.end();
            newOutput.add(TimeRange.fromStartEnd(startMinute, endMinute, false));
          }
          else{
            int startMinute = object.start();
            int endMinute = event.getWhen().start();
            newOutput.add(TimeRange.fromStartEnd(startMinute, endMinute, false)); 
          }
        }
        else{
            newOutput.add(object);
        }
      }
      output = newOutput;
    }
    Collection<TimeRange> result;
    result = output.stream().filter(anyRange -> anyRange.duration() >= request.getDuration()).collect(Collectors.toCollection(ArrayList::new));
    return result;
  }
}
