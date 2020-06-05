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

var commentID = 0;

function addComment() {
    const commentContainer = document.getElementById('commentContainer');
    
    console.log(commentContainer.value);
    commentContainer.value = "";
}

function fetchComments(numComments){
    console.log("page 1");
    fetchCommentsPerPage(numComments, 1);
}

function fetchCommentsPerPage(numComments, page) {
    var totalComments = 0;
    document.getElementById('commentListContainer').innerHTML = "";
	fetch('/comment').then(response => response.json()).then((comments) => {
        for(i in comments){
            // console.log(comments[i].text);
            // console.log(i+" is between? "+(numComments * (page-1))+" and "+(numComments * page));
            if(i < numComments * page && i >= numComments * (page-1)){
                displayComment(comments[i]);
            }
            totalComments = i;
            // console.log(totalComments);
        }
    var pageNum = 1;
    var htmlStr = "Page ";
    // console.log("Total Comment "+totalComments+" and page num "+pageNum);
    document.getElementById('pageContainer').innerHTML ="";
    while(totalComments > 0){
        // console.log("Total Comment "+totalComments+" and page num "+pageNum);
        htmlStr += (pageNum === page) ? "<span class='page_number selected'" : "<span class='page_number'";
        htmlStr += " onclick='fetchCommentsPerPage("+numComments+","+pageNum+");'>"+pageNum+"</span>";
        pageNum++;
        totalComments-=numComments;
    }
    document.getElementById('pageContainer').innerHTML += htmlStr;
	});
}

function deleteComments() {
    fetch("/delete-data", {method: "post"});
    fetchComments(10);
}

/*
prevent user from writing html content by first creating the html to hold the comment
then adding the parts of the comment as inner text to the html elements
*/
function displayComment(comment){
		document.getElementById('commentListContainer').innerHTML += 
            "<div class='comment'><span id='commentDate"+commentID+"'></span><h3 id='commentName"+commentID+"'></h3><p id='commentText"+commentID+"'></p></div>";
        document.getElementById("commentDate"+commentID).innerText = comment.date;
        document.getElementById("commentName"+commentID).innerText = comment.name;
        document.getElementById("commentText"+commentID).innerText = comment.text;
        commentID++;
}