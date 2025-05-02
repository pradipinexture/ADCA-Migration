$("#search-form").submit(function (event) {
  var x = document.getElementById("search-input-bar").value;
  window.location.href = "/group/portal/search?q=" + x;
  event.preventDefault();
});