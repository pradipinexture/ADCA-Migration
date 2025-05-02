var docsArr = [];
var categoriesArr=[];

getCategories();
    function getCategories() {

        $.ajax({
            url: `/o/adc-dxp-services/related/categoriesList?type=knowledge-sharing`,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + Liferay.authToken,
                "languageId": Liferay.ThemeDisplay.getLanguageId(),
            },
            success: function (categories) {
                $('#categoriesSelect').append(`<option value=-1>-</option>`)
                for (let cat of categories) {
                    $('#categoriesSelect').append(`<option value="${cat.value}">${cat.label}</option>`)

                }
            },
            error: function (error) {
                // Handle the error
                console.log('this is response error');
                console.log(error);
            }
        });

    }



$('#categoriesSelect').on('change' ,function(){
    categoriesArr=[];
   categoriesArr.push($(this).val());
  
})


$('.clear-form-btn').on('click', function () {

    for (let id of docsArr) {

        deleteAttachment(id);


    }

    $('#ks_form').find('.form-control').val("")

});


async function uploadFiles(file) {
    var formData = new FormData();
    formData.append('file', file);
    var documentId = "";

    try {
        const response = await fetch('/o/headless-delivery/v1.0/document-folders/2414012/documents', {
            method: 'POST',
            headers: {
                Authorization: "Bearer " + Liferay.authToken,
                languageId: Liferay.ThemeDisplay.getLanguageId(),
                groupId: Liferay.ThemeDisplay.getSiteGroupId(),
            },
            body: formData
        });

        const data = await response.json();
        documentId = data.id;
    } catch (error) {
        console.error(error);
    }

    return documentId;
}



function deleteAttachment(documentId) {
    fetch(`/o/headless-delivery/v1.0/documents/${documentId}`, {
        method: 'DELETE',
        headers: {
            Authorization: "Bearer " + Liferay.authToken,
            languageId: Liferay.ThemeDisplay.getLanguageId(),
            groupId: Liferay.ThemeDisplay.getSiteGroupId(),
        },
    })
        .then(response => {
            if (response.ok) {
                console.log('File deleted successfully.');
            } else {
                console.error('Error deleting the file.');
            }
        })
        .catch(error => {
            console.error('An error occurred while deleting the file:', error);
        });

}


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
            valid = true;
        }
    });

    if (!valid) {
       
        return;
    }

    // Get all form values
    var formData = {};
    $(this).find('input[type="text"], textarea').each(function () {
        var fieldName = $(this).attr('id');
        var fieldValue = $(this).val();
        formData[fieldName] = fieldValue;
    });


    $(this).find('.attachment-row').each(function (index) {
        let enId = '';
        let arId = '';

        enId = $(this).find('input[type="file"].attachment').eq(0).attr('documentId');
        arId = $(this).find('input[type="file"].attachment').eq(1).attr('documentId');


        let contentFieldValue = {
            "contentFieldValue": {
                "document": {
                    "id": enId
                }
            },
            "name": "KS_attachments",
            "contentFieldValue_i18n": {
                "en-US": {
                    "document": {
                        "id": enId
                    }
                },
                "ar-AE": {
                    "document": {
                        "id": arId
                    }
                }
            }
        };

        contentFieldValuesArr.push(contentFieldValue);
    });


    let enId = '';

    enId = $(this).find('input[type="file"].poster').eq(0).attr('documentId');

    let posters = {
        "contentFieldValue": {

            "image": {
                "id": enId
            }
        },
        "name": "KS_poster",
        "contentFieldValue_i18n": {
            "en-US": {
                "image": {
                    "id": enId
                }
            },
            "ar-AE": {
                "image": {
                    "id": enId
                }
            }
        }
    }

    contentFieldValuesArr.push(posters)

    // Perform your action with the form data
    postNewArticle(formData, contentFieldValuesArr);



});


function postNewArticle(formData, contentFieldValuesArr) {
  
  $('.loader-container').removeClass('d-none');
  $('loader-container').addClass('d-flex');
    let fieldsArr = contentFieldValuesArr;
    let currentDate = new Date();
    let formattedDate = currentDate.toISOString();

    fieldsArr.push({
        "contentFieldValue": {
            "data": formData['description']
        },
        "name": "KS_body",
        "contentFieldValue_i18n": {
            "en-US": {
                "data": formData['description']
            },
            "ar-AE": {
                "data": formData['description-ar']
            }
        }
    })
    let obj = {
        "title": formData['title-en'],
        "title_i18n": {
            "en-US": formData['title-en'],
            "ar-AE": formData['title-ar']
        },
        "availableLanguages": [
            "en-US",
            "ar-AE"
        ],
        "contentFields": fieldsArr,
        "contentStructureId": 2430772,
        "datePublished": formattedDate,
        "siteId": Liferay.ThemeDisplay.getSiteGroupId(),
        "taxonomyCategoryIds": categoriesArr
    }
 
    $.ajax({
        url: '/o/headless-delivery/v1.0/structured-content-folders/2430776/structured-contents',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + Liferay.authToken,
            'languageId': Liferay.ThemeDisplay.getLanguageId(),
            'groupId': Liferay.ThemeDisplay.getSiteGroupId()
        },
        data: JSON.stringify(obj),
        success: function(response) {
            // Handle the response
              console.log(response);
              $('.loader-container').removeClass('d-none');
              $('loader-container').addClass('d-flex');
              $('#ks_form').find('.form-control').val("");
            
              window.location.href="/group/portal/knowledge-share";

        },
        error: function(error) {
            // Handle errors
            console.log("this is error log");
            console.error(error);
            alert('could not add new article!')
        }
    });



}

$('.btn-add-attachment').on('click', function () {
    var attachmentsContainer = $('.attachments-container');
    var counter = $('.attachment-row').length + 1;
    $('.btn-remove-attachment').removeClass('d-none');


    var newAttachmentRow = `
      <div class="row attachment-row py-2 align-items-center" id="attachment-row-${counter}">
        <div class="col-md-6">
         
            <label for="attachment-${counter}" class="form-label">Attachment</label>
            <input type="file" class="form-control attachment" id="attachment-${counter}">
         
        </div>
        <div class="col-md-5">
         
            <label for="attachment-ar-${counter}" class="form-label dir-rtl">المرفق</label>
            <input type="file" class="form-control attachment" id="attachment-ar-${counter}">
          
        </div>
        
           <div class="col-md-1 text-right">
           <button type="button" class="btn rounded-sm-btn mt-2 btn-remove-attachment" data-row="attachment-row-${counter}"><i class="icon-remove text-danger"></i></button>
         
         </div>
      </div>
    `;

    attachmentsContainer.append(newAttachmentRow);
});


$(document).on('click', '.btn-remove-attachment', function () {

    var attachmentRows = $('.attachment-row').length;
    if (attachmentRows > 1) {
        var deleteRow = $(this).attr('data-row');
        if (deleteRow) {
            $('#' + deleteRow).find('.attachment').each(function () {
              let docToBeRemoved=  $(this).attr('documentId');
              if(docToBeRemoved){
                deleteAttachment(docToBeRemoved);
              }
              
            });

            $('#' + deleteRow).remove();

        }
    }


    if ($('.attachment-row').length <= 1) {
        $('.btn-remove-attachment').addClass('d-none');
    }

})

var filesArr = [];
$(document).on('change', 'input[type="file"]', async function () {


    var fileInput = document.getElementById($(this).attr('id'));
    var file = fileInput.files[0];
    filesArr.push(file.name);
    var hasDuplicatesResult = hasDuplicates(filesArr);


    if (hasDuplicatesResult) {

        alert('File already Exists')
        $(this).val("");
        filesArr.pop();

    } else {
        console.log('Array does not have duplicates.');

        if (file) {
            let time = new Date();
            var fileNameParts = file.name.split('.');
            var fileExtension = fileNameParts.pop();
            var modifiedFileName = fileNameParts.join('.') + '-' + time.getTime() + '.' + fileExtension;
            var modifiedFile = new File([file], modifiedFileName, { type: file.type });
            let docId = await uploadFiles(modifiedFile);
            $(this).attr('documentId', docId);

        }
    }





});

function hasDuplicates(arr) {
    var duplicateFound = arr.find(function (element, index) {
        return arr.indexOf(element) !== index;
    });

    return duplicateFound !== undefined;
}