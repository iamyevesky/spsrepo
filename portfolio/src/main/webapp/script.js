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
function addRandomThanosQuote() {
  const greetings =
      ['The hardest choices require the strongest wills.',
       'I ask you to what end? Dread it. Run from it. Destiny arrives all the same',
       'I know what it\'s like to lose. To feel so desperately that you\'re right, yet to fail nonetheless. It\'s frightening'
        + 'turns the legs to jelly.',
       'Perfectly balanced, as all things should be.',
       'I do. You are not the only one cursed with knowledge.',
       'I finally rest and watch the sun rise on a grateful universe.',
       'Fine! I will do it myself.',
       'I ignored by destiny once. I cannot do that again, even for you.',
       'With all the six stones I could simply snap my fingers and they'+
       'would cease to exist. I call that mercy'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greetingContainer');
  greetingContainer.innerText = greeting;
}

function getThanosQuote(){
    fetch("/data").then(response => response.text()).then(quote => 
    {
        var thanosSection = document.getElementById("quoteSection");
        thanosSection.innerText = quote;
    });
}

function getLoginForm(){
    fetch("/login").then(response => response.text()).then(textHTML =>
    {
        var loginSection = document.getElementById("loginSection");
        loginSection.innerHTML = textHTML;
    });
}

function getNameForm(){
    fetch("/name").then(response => response.text()).then(textHTML =>
    {
        var loginSection = document.getElementById("loginSection");
        loginSection.innerHTML = textHTML;
    });
}

function getJSONobject(){
    fetch("/data").then(response => response.json()).then(object =>
    {
        var quoteSection = document.getElementById("commentSection");
        buildList(quoteSection, object);
    })
}

function buildList(htmlObject, jsonObject){
    htmlObject.innerHTML = '';
    for(let i = 0; i < jsonObject.length; i++){
        htmlObject.appendChild(createListElement(jsonObject[i]));
    }
}

function createListElement(list){
    var element = document.createElement("form");
    element.action = "/deleteComment";
    element.method = "POST";
    element.innerHTML = '';
    element.appendChild(createListParagraphElement(list[1]));
    element.appendChild(createInputElement(list[0]));
    element.appendChild(generateSubmitElement());
    return element;
}

function generateSubmitElement(){
    var element = document.createElement("input");
    element.type = "submit";
    element.value = "Delete comment";
    return element;
}

function createListParagraphElement(string){
    var element = document.createElement("p");
    element.innerText = string;
    return element;
}

function createInputElement(id){
    var element = document.createElement("input");
    element.type = "hidden";
    element.name = "idInput";
    element.value = id;
    return element;
}
