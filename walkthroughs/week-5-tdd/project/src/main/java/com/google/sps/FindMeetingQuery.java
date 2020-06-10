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
import java.util.HashSet;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
  //   throw new UnsupportedOperationException("TODO: Implement this method.");
    long duration = request.getDuration();
    Collection<String> attendees =  request.getAttendees();
    Collection<TimeRange> blockedtimes = new ArrayList<TimeRange>();
    Collection<TimeRange> result = new ArrayList<TimeRange>();
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
    Collection<TimeRange> condensedblockedtimes = new ArrayList<TimeRange>();
    for(int i = 0; i < blockedtimes.size() - 1; i++) {
      TimeRange first = blockedtimes.get(i);
      TimeRange next = blockedtimes.get(i+1);
      if (first.contains(next)) {
        blockedtimes.remove(next);
        i--;
      } else if (first.overlaps(next)) {
        blockedtimes.add(i, new TimeRange(first.start(), next.end()));
        i--;
      } else {
        condensedblockedtimes.add(i, blockedtimes.get(i));
      }
    }
    for(int i = 0; i < condensedblockedtimes.size(); i++) {
      if (i == 0) {
        result.add(i, new TimeRange(TimeRange.START_OF_DAY, condensedblockedtimes.get(i).start()));
      } else {
        result.add(i, new TimeRange(condensedblockedtimes.get(i-1).end(), condensedblockedtimes.get(i).start()));
      }
    }
    if (condensedblockedtimes.size() > 0) {
      result.add(newTimeRange(condensedblockedtimes.get(condensedblockedtimes.size()-1), TimeRange.END_OF_DAY));
    }
    return result;

  }
}
