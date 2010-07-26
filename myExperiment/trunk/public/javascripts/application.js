// Place your application-specific JavaScript functions and classes here
// This file is automatically included by javascript_include_tag :defaults

// http://labnol.blogspot.com/2006/01/add-to-favorites-ie-bookmark-firefox.html
function CreateBookmarkLink() {

title = "<%=h title %>"; 
url = "<%= url %>";

if (window.sidebar) { // Mozilla Firefox Bookmark
	window.sidebar.addPanel(title, url,"");
} else { // IE, Opera, etc
	return true;
}
}

var TopicForm = {
  editNewTitle: function(txtField) {
    $('new_topic').innerHTML = (txtField.value.length > 5) ? txtField.value : 'New Topic';
  }
}

var LoginForm = {
  checkLogin: function(txt) {
    if(txt.value.match(/^https?:\/\//)) {
      $('password_fields').hide();
    } else {
      $('password_fields').show();
    }
  }
}

var EditForm = {
  // show the form
  init: function(postId) {
    $('edit-post-' + postId + '_spinner').show();
    this.clearReplyId();
  },

  // sets the current post id we're editing
  setReplyId: function(postId) {
    $('edit').setAttribute('post_id', postId.toString());
    $('posts-' + postId + '-row').addClassName('editing');
    if($('reply')) $('reply').hide();
  },
  
  // clears the current post id
  clearReplyId: function() {
    var currentId = this.currentReplyId()
    if(!currentId || currentId == '') return;

    var row = $('posts-' + currentId + '-row');
    if(row) row.removeClassName('editing');
    $('edit').setAttribute('post_id', '');
  },
  
  // gets the current post id we're editing
  currentReplyId: function() {
    return $('edit').getAttribute('post_id');
  },
  
  // checks whether we're editing this post already
  isEditing: function(postId) {
    if (this.currentReplyId() == postId.toString())
    {
      $('edit').show();
      $('edit_post_body').focus();
      return true;
    }
    return false;
  },

  // close reply, clear current reply id
  cancel: function() {
    this.clearReplyId();
    $('edit').hide()
  }
}

var ReplyForm = {
  // yes, i use setTimeout for a reason
  init: function() {
    EditForm.cancel();
    $('reply').toggle();
    $('post_body').focus();
    // for Safari which is sometime weird
//    setTimeout('$(\"post_body\").focus();',50);
  }
}

//Event.addBehavior({
//  '#search,#monitor_submit': function() { this.hide(); }
//})

function redirectToAddToPack() {
  var url = $("add_to_pack_selection").value;
  location.href = url;
}

function checkAll(checkboxes){
  for (var i=0; i<checkboxes.length; i++){
    checkbox = document.getElementById(checkboxes[i]);
    checkbox.checked="checked";
  }
}

function uncheckAll(checkboxes){
  for (var i=0; i<checkboxes.length; i++){
    checkbox = document.getElementById(checkboxes[i]);
    checkbox.checked="";
  }
}

