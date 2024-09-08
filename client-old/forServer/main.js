
function cleanSearchArea(){
	var searchResultDiv = document.getElementById("searchResult");
	searchResultDiv.innerHTML = "";

	var searchTextEl = document.getElementById('search_text');
	searchTextEl.value = '';
}

function searchClass() {
  var e = document.getElementById("DocType");
  var docTypeVal = e.options[e.selectedIndex].value;

  var searchEl = document.getElementById("search_text");
  var searchElVal = searchEl.value;

  var searchResultDiv = document.getElementById("searchResult");
  searchResultDiv.innerHTML = "";

  var elArr = document.getElementsByClassName(docTypeVal + searchElVal);

  var newElArr = Array.prototype.slice.call(elArr, 0);
  Array.prototype.forEach.call(newElArr, function(el) {
    var tr = document.createElement("tr");
    var td1 = document.createElement("td");
    var td2 = document.createElement("td");
    var td3 = document.createElement("td");
    var td4 = document.createElement("td");
    var td5 = document.createElement("td");
    var td6 = document.createElement("td");
    var td7 = document.createElement("td");

    var attribute = el.getAttribute("href");
    var path = el.getAttribute("path");
    var Type = el.getAttribute("type");
    var Description = el.getAttribute("description");
    var certificate = el.getAttribute("certificate");
    var comment = el.getAttribute("comment");
    var uploadDate = el.getAttribute("uploadDate");
    var fileName = el.getAttribute("fileName");
    var text2 = document.createTextNode("Path: " + path);
    var text3 = document.createTextNode("Type: " + Type);
    var text4 = document.createTextNode("Description: " + Description);
    var text5 = document.createTextNode("Certificate: " + certificate);
    var text6 = document.createTextNode("Comment: " + comment);
    var text7 = document.createTextNode("Upload Date: " + uploadDate);
    var text8 = document.createTextNode("File Name: " + fileName);

    searchResultDiv.appendChild(el.cloneNode(true));
    td1.appendChild(text2);
    td2.appendChild(text3);
    td3.appendChild(text4);
    td4.appendChild(text5);
    td5.appendChild(text6);
    td6.appendChild(text7);
    td7.appendChild(text8);
    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);
    tr.appendChild(td4);
    tr.appendChild(td5);
    tr.appendChild(td6);
    tr.appendChild(td7);

    searchResultDiv.appendChild(tr);
  });
}

function showEmptyFolders() {
  var elArr = document.getElementsByClassName("empty-folder");
  Array.prototype.forEach.call(elArr, function(el) {
    el.classList.toggle("empty-folder");
  });
}

function hiddenEmptyFolders() {
  var elArr = document.getElementsByClassName("nested");
  var emptyEls = Array.prototype.filter.call(elArr, function(el) {
    return el.getElementsByTagName("LI").length === 0;
  });

  Array.prototype.forEach.call(emptyEls, function(el) {
    var previousElementSibling = el.previousElementSibling;
    if (previousElementSibling.getElementsByTagName("A").length === 0)
      previousElementSibling.classList.toggle("empty-folder");
  });
}

function shareDownloadedProject() {
  let emails = window.prompt("Please enter email addresses for sharing. the emails shall be separated with the ';' character");

  if (emails?.length) {
    const url = "https://visoftapp1.visoft-eng.com/archive/api/share";

    const requestOptions = {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json"
      },
      body: JSON.stringify({
        emails: emails.split(";"),
        folder: document.getElementById("folder").innerHTML
      })
    };

    var resp = fetch(url, requestOptions)
        .catch(err => console.log(err));
  }
}
