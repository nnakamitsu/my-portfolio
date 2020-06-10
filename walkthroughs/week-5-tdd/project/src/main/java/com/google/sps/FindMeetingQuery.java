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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
  //   throw new UnsupportedOperationException("TODO: Implement this method.");
    long duration = request.getDuration();
    Collection<String> attendees =  request.getAttendees();
    List<TimeRange> blockedtimes = new ArrayList<TimeRange>();
    List<TimeRange> result = new ArrayList<TimeRange>();
    for(Event event : events) {
      boolean yes = false;
      for(String attendee : event.getAttendees()) {
        if (attendees.contains(attendee)) {
          yes = true;
          break;
        }
      }
      if (yes) {
        blockedtimes.add(event.getWhen());
      }
    }
    Collections.sort(blockedtimes, TimeRange.ORDER_BY_START);
    List<TimeRange> condensedblockedtimes = new ArrayList<TimeRange>();
    for(int i = 0; i < blockedtimes.size() - 1; i++) {
      TimeRange first = blockedtimes.get(i);
      TimeRange next = blockedtimes.get(i+1);
      if (first.contains(next)) {
        blockedtimes.remove(next);
        i--;
      } else if (first.overlaps(next)) {
        blockedtimes.add(i, TimeRange.fromStartEnd(first.start(), next.end(), false));
        i--;
      } else {
        condensedblockedtimes.add(i, blockedtimes.get(i));
      }
    }
    if (blockedtimes.size() > 0) {
      condensedblockedtimes.add(blockedtimes.get(blockedtimes.size() - 1));
    }
    for(int i = 0; i < condensedblockedtimes.size(); i++) {
      if (i == 0) {
        result.add(i, TimeRange.fromStartEnd(TimeRange.START_OF_DAY, condensedblockedtimes.get(i).start(), false));
      } else {
        result.add(i, TimeRange.fromStartEnd(condensedblockedtimes.get(i-1).end(), condensedblockedtimes.get(i).start(), false));
      }
    }
    if (condensedblockedtimes.size() > 0) {
      result.add(TimeRange.fromStartEnd(condensedblockedtimes.get(condensedblockedtimes.size()-1).end(), TimeRange.END_OF_DAY, true));
    } else {
      result.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    }

    // Ensure that all results are large enough durations.
    for(int i = 0; i < result.size(); i++) {
      if (result.get(i).duration() < duration) {
        result.remove(i);
        i--;
      }
    }
    return result;

  }
}
