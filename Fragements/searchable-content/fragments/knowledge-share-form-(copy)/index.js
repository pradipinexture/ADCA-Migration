$('#ks_form').submit(async function (e) {
    e.preventDefault();

    // Validate fields are not empty
    let contentFieldValuesArr = [];
    var valid = true;
    $(this).find('input[type="text"], textarea').each(function () {
        if (!$(this).val()) {
            valid = false;
            $(this).addClass('is-invalid');
        } else {
            $(this).removeClass('is-invalid');
         }
    });


  
    if (!valid) {

        $('.emptyInput_msg').removeClass('d-none')

        return;
    }else $('.emptyInput_msg').addClass('d-none')



});


$('.fillEmptyTitleInputBtn').on('click', function(){

fillTitles();
 })

$(".fillEmptyDescInputBtn").on("click", function () {
   fillDescriptions();
});

function fillTitles(){

  let titleInput_ar = $("#title-inputs #title-ar");
  let titleInput_en = $("#title-inputs #title-en");

  if (!titleInput_ar.val()) {
    $(titleInput_ar).val(titleInput_en.val());
  }

  if (!titleInput_en.val()) {
    $(titleInput_en).val(titleInput_ar.val());
  }
}

function fillDescriptions(){
  let descriptionInput_ar = $("#description-inputs #description-ar");
  let descriptionInput_en = $("#description-inputs #description");

  if (!descriptionInput_ar.val()) {
    $(descriptionInput_ar).val(descriptionInput_en.val());
  }

  if (!descriptionInput_en.val()) {
    $(descriptionInput_en).val(descriptionInput_ar.val());
  }
}