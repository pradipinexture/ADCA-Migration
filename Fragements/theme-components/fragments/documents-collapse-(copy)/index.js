let openedCollapseId= '';
let previousCollapsePosition ='';

$('#accordion .card-header button.btn').on('click',function(){
  
  $(this).find('i').toggleClass('icon-chevron-down');
  $(this).find('i').toggleClass('icon-chevron-up');
  openedCollapseId = $(this).attr('data-target');
 localStorage.setItem('openedCollapseId', openedCollapseId);
 
// let openedCollapsePosition = $(this).closest('.card-header').getBoundingClientRect().top + window.pageYOffset;
// localStorage.setItem('openedCollapsePosition', openedCollapsePosition);


});

 

// Save the ID to local storage
 

// Get the previously opened collapse ID from local storage
previousCollapseId = localStorage.getItem('openedCollapseId');
//previousCollapsePosition = localStorage.getItem('openedCollapsePosition');


if (previousCollapseId ) {
  // Find the collapse element by ID
  const previousCollapse = document.querySelector(`${previousCollapseId}`);

  if (previousCollapse) {
    // Open the collapse
    // window.scrollTo({
    //   top: previousCollapsePosition,
    //   behavior: 'smooth'
    // });
    previousCollapse.classList.add('show');
  }
}