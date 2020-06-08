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
    fetchCommentsPerPage(numComments, 1);
}

function fetchCommentsPerPage(numComments, page) {
    document.getElementById('commentListContainer').innerHTML = "";
    document.getElementById('pageContainer').innerHTML ="";
	fetch('/comment?numComments='+numComments+"&pageNum="+page).then(response => response.json()).then((commentsList) => {
        console.log(commentsList);
        var comments = commentsList.comments;
        var totalComments = commentsList.numComments;
        for(i in comments){
            displayComment(comments[i]);
        }
        var pageNum = 1;
        var htmlStr = "Page ";
        while(totalComments > 0){
            htmlStr += (pageNum === page) ? "<span class='page_number selected'" : "<span class='page_number'";
            htmlStr += " onclick='fetchCommentsPerPage("+numComments+","+pageNum+");'>"+pageNum+"</span>";
            pageNum++;
            totalComments-=numComments;
        }
        document.getElementById('pageContainer').innerHTML += htmlStr;
	});
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