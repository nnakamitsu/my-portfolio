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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  
  const quotes =
      ["\"It's hard to beat a person who never gives up.\" -Babe Ruth", 
      '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  var i = 0;
  var txt = quote;
  var speed = 50;

  function typeWriter() {
    if (i < txt.length) {
      document.getElementById("quotes").innerHTML += txt.charAt(i);
      i++;
      setTimeout(typeWriter, speed);
    }
}
typeWriter();
}

async function getData() {
  console.log('Getting Data');
  const response = await fetch('/data');
  const data = await response.text();
  console.log(data)
  document.getElementById('data-container').innerText = data;
}

function loadTasks() {
  const commentCount = document.getElementById('maxcomments');
  console.log(commentCount.value)
  fetch('/data').then(response => response.json()).then((tasks) => {
    const taskListElement = document.getElementById('task-list');
    tasks.forEach((task) => {
      console.log(task.title)
      taskListElement.appendChild(createTaskElement(task));
    })
  });
}

function getMessages() {
  const commentCount = document.getElementById('maxcomments');
  console.log(commentCount.name)
  document.getElementById('task-list').innerHTML = "";
  fetch('/data?maxcomments=' + commentCount.value).then(response => response.json()).then((tasks) => {
    const taskListElement = document.getElementById('task-list');
    tasks.forEach((task) => {
      console.log(task.title)
      taskListElement.appendChild(createTaskElement(task));
    })
  });
}

function sortComments() {
  const sort = document.getElementById('sort');
  console.log(sort.value)
  document.getElementById('task-list').innerHTML = "";
  fetch('/data?sort=' + sort.value).then(response => response.json()).then((tasks) => {
    const taskListElement = document.getElementById('task-list');
    tasks.forEach((task) => {
      console.log(task.title)
      taskListElement.appendChild(createTaskElement(task));
    })
  });
}

function updateCount() {
  location.replace("index.html")
}

function createTaskElement(task) {
  const taskElement = document.createElement('li');
  taskElement.className = 'task collection-item';

  const titleElement = document.createElement('span');
  titleElement.innerText = task.title;

  const nameElement = document.createElement('span');
  if (task.name === undefined || task.name === "") {
    nameElement.innerHTML = "-- Anonymous".italics().bold();
  } else {
    nameElement.innerHTML = ("--" + task.name).italics().bold();
  }
  nameElement.style.margin = "15px"

  const timeElement = document.createElement('span');
  var date = new Date(task.timestamp);
  timeElement.innerText = date.toString().slice(0,24);
  timeElement.style.float = "right";
  timeElement.style.marginRight = "10px";

  var deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.style.float = "right";
  deleteButtonElement.addEventListener('click', () => {
    deleteTask(task);

    // Remove the task from the DOM.
    taskElement.remove();
  });


  taskElement.appendChild(titleElement);
  taskElement.appendChild(nameElement);
  taskElement.appendChild(deleteButtonElement);
  taskElement.appendChild(timeElement);
  return taskElement;
}

function deleteTask(task) {
  const params = new URLSearchParams();
  params.append('id', task.id);
  fetch('/delete', {method: 'POST', body: params});
}

/** Creates a Google map and adds it to the page. */
var marker;
var marker2;
var marker3;
var marker4;
var marker5;
var currentBouncer = null;

var isBouncing = false;

var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';

function createMap() {
  var wheeler = {lat: 37.871032, lng: -122.258893};
  var marugame = {lat: 37.873307, lng: -122.268291};
  var sweetheart = {lat: 37.868004, lng: -122.2577686};
  var moffitt = {lat: 37.872778, lng: -122.260665};
  var rsf = {lat: 37.868571, lng: -122.262752};
  var center = {lat:37.871520, lng:-122.261509};

  var icon1 = {url: iconBase + 'dining.png', // url
    scaledSize: new google.maps.Size(40, 40), // scaled size
  };
  
  function makeIcon(name, scale) {
    return {url: iconBase + name, scaledSize: new google.maps.Size(scale, scale)};
  }

  var icon1 = makeIcon("ruler.png", 40)
  var icon2 = makeIcon("dining.png", 40);
  var icon3 = makeIcon("snack_bar.png", 40);
  var icon4 = makeIcon("schools.png", 40);
  var icon5 = makeIcon("play.png", 40);

  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: center, zoom: 14});

  marker = new google.maps.Marker({position: wheeler, map: map, animation: null, icon: icon1});
  marker.addListener('click', () => {
    editMarkerText("<b>Wheeler Hall:</b> The OG lecture hall where I get to witness \
    my legendary professors live in action.")});
  marker.addListener('click', () => {
    toggleBounce(marker)});

  marker2 = new google.maps.Marker({position: marugame, map: map, animation: null, icon: icon2});
  marker2.addListener('click', () => {
    editMarkerText("<b>Marugame Udon:</b> Undoubtedly my go to restaurant in Berkeley. I \
    highly recommend the Curry Nikutama!")});
  marker2.addListener('click', () => {
    toggleBounce(marker2)});

  marker3 = new google.maps.Marker({position: sweetheart, map: map, animation: null, icon: icon3});
  marker3.addListener('click', () => {
    editMarkerText("<b>SweetHeart Cafe & Tea:</b> My go to late night snack. You can't go wrong \
    with the <i>Hong Kong Style Lemon Tea</i> and <i>Spicy Popcorn Chicken</i>.")});
  marker3.addListener('click', () => {
    toggleBounce(marker3)});

  marker4 = new google.maps.Marker({position: moffitt, map: map, animation: null, icon: icon4});
  marker4.addListener('click', () => {
    editMarkerText("<b>Moffitt Library:</b> The savior where late night grinds are made possible.")});
  marker4.addListener('click', () => {
    toggleBounce(marker4)});

  marker5 = new google.maps.Marker({position: rsf, map: map, animation: null, icon: icon5});
  marker5.addListener('click', () => {
    editMarkerText("<b>Recreational Sports Facility:</b> My friends and I go to the RSF whenever we want to wind down \
    and shoot hoops.")});
  marker5.addListener('click', () => {
    toggleBounce(marker5)});
}

function toggleBounce(m) { 
        console.log(m.getAnimation());
        if (m.getAnimation() !== null) {
          m.setAnimation(null);
          isBouncing = false;
          currentBouncer = null;
          editMarkerText("");
        } else if (currentBouncer === null){
          m.setAnimation(google.maps.Animation.BOUNCE);
          isBouncing = true;
          currentBouncer = m;
        } else {
          currentBouncer.setAnimation(null);
          currentBouncer = m;
          m.setAnimation(google.maps.Animation.BOUNCE);
        }
      }

function editMarkerText(message) {
  document.getElementById('markerText').innerHTML = message;
}

function loadPage() {
  loadTasks();
  createMap();
}
