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

function addComment() {
    const commentContainer = document.getElementById('commentContainer');
    
    console.log(commentContainer.value);
    commentContainer.value = "";
}

function fetchComments(numComments) {
    document.getElementById('commentListContainer').innerHTML = "";
	fetch('/comment').then(response => response.json()).then((comments) => {

        for(i in comments){
            if(i < numComments){
                displayComment(comments[i]);
            }
        }
	});
}

function deleteComments() {
    fetch("/delete-data", {method: "post"});
    fetchComments(10);
}

function displayComment(comment){
		document.getElementById('commentListContainer').innerHTML += 
            "<div class='comment'><span>"+comment.date+"</span><h3>"+comment.name+"</h3><p>"+comment.text+"</p></div>";
}