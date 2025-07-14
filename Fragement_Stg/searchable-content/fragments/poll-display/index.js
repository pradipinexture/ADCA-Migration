const modal = document.getElementById('select-form-modal');
const editModal = document.getElementById('edit-form-modal');
const addModal = document.getElementById('add-form-modal');
const selectFormLink = document.getElementById('select-form-link');
const addFormLink = document.getElementById('add-form-link');
const editFormLink = document.getElementById('edit-form-link');
const closeBtns = document.querySelectorAll('.close');
const cancelBtn = document.getElementById('cancel-btn');
const saveBtn = document.getElementById('save-btn');
const pollList = document.getElementById('poll-list');
const iframe = document.getElementById('poll-iframe');
const editFormIframe = document.getElementById('edit-form-iframe');
const pollBox = document.querySelector('.poll-box');
const surveyContainer = document.getElementById('survey-container');
const surveyHeading = document.getElementById('survey-heading');
const surveyQuestion = document.getElementById('survey-question');
const surveyResultsTable = document.getElementById('survey-results-table');
const totalVotes = document.getElementById('total-votes');
const loader = document.getElementById('loader');
const editModalCloseBtn = document.querySelector('#edit-form-modal .close');

let pollsData = [];
let filteredPolls = []; // New array to store filtered polls
let currentPage = 1;
const itemsPerPage = 5;

const NO_PERMISSION_MESSAGES = [
  'You do not have the roles required to access this portlet.',
  'أنت لا تملك الأدوار اللازمة للوصول الى هذه البورتلت'
];

const pollIframe = document.getElementById('poll-iframe');
pollIframe.style.visibility = 'hidden';

pollIframe.addEventListener('load', () => {
  try {
    const iframeDoc = pollIframe.contentDocument;
    if (!iframeDoc) return;

    const locale = iframeDoc.querySelector('.locale-actions');
    if (locale && locale.parentElement) {
      locale.parentElement.style.display = 'none';
    }

    pollIframe.style.visibility = 'visible';

  } catch (e) {
    console.error('Error customizing iframe content:', e);
    pollIframe.style.visibility = 'visible';
  }
});

// Permission check and hide the buttons add and edit poll
function checkIframeForPermission(iframe, url, buttonId) {
  return new Promise((resolve, reject) => {
    iframe.onload = () => {
      try {
        const doc = iframe.contentDocument || iframe.contentWindow.document;
        const text = doc.body.textContent || '';
        
        const hasNoPermission = NO_PERMISSION_MESSAGES.some(msg =>
          text.includes(msg)
        );

        if (hasNoPermission) {
          resolve(false);
        } else {
          resolve(true);
        }

      } catch (e) {
        resolve(false);
      }
    };
    iframe.src = url;
  });
}

async function validateAddPermission() {
  const addIframe = document.getElementById('add-form-iframe');
  const addButton = document.getElementById('add-form-link');
  
  const addUrl = '/group/portal/~/control_panel/manage?p_p_id=com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view';
  
  if (!addIframe || !addButton) return;
  const hasPermission = await checkIframeForPermission(addIframe, addUrl, 'add-form-link');
  if (hasPermission) addButton.style.display = 'flex';
  return hasPermission;
}

async function validateEditPermission(formInstanceId) {
  const editIframe = document.getElementById('edit-form-iframe');
  const editButton = document.getElementById('edit-form-link');
  
  const editUrl = `/group/control_panel/manage?p_p_id=com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet&p_p_lifecycle=0&_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_mvcRenderCommandName=%2Fadmin%2Fedit_form_instance&_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_formInstanceId=${formInstanceId}`;
  
  if (!editIframe || !editButton) return;
  const hasPermission = await checkIframeForPermission(editIframe, editUrl, 'edit-form-link');
  if (hasPermission) editButton.style.display = 'flex';
  return hasPermission;
}

validateAddPermission();
validateEditPermission(selectedFormId);

// Open modal and fetch data for Select Form
selectFormLink.addEventListener('click', async () => {
    modal.style.display = 'flex';
    try {
        const response = await fetch('/o/polls?groupId=' + themeDisplay.getScopeGroupId()+ '&languageId=' + Liferay.ThemeDisplay.getLanguageId().replace("_","-"), {
            headers: {
                'x-csrf-token': Liferay.authToken,
				'languageId': Liferay.ThemeDisplay.getLanguageId()
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        if (data.status === 'success' && Array.isArray(data.data)) {
            pollsData = data.data;
            filteredPolls = [...pollsData]; // Initialize filteredPolls with all polls
            currentPage = 1; // Reset to first page when loading new data
            if (pollsData.length === 0) {
                document.getElementById('poll-items-container').innerHTML = '<p>No polls available.</p>';
                document.getElementById('page-info').textContent = 'Page 1 of 1';
                document.getElementById('prev-btn').disabled = true;
                document.getElementById('next-btn').disabled = true;
            } else {
                renderPollList();
                // Re-attach event listeners for pagination buttons after rendering
                document.getElementById('prev-btn').addEventListener('click', handlePrevClick);
                document.getElementById('next-btn').addEventListener('click', handleNextClick);
            }
        } else {
            document.getElementById('poll-items-container').innerHTML = '<p>Error: Invalid response from server.</p>';
            document.getElementById('page-info').textContent = 'Page 1 of 1';
            document.getElementById('prev-btn').disabled = true;
            document.getElementById('next-btn').disabled = true;
        }
    } catch (error) {
        console.error('Error fetching polls:', error);
        document.getElementById('poll-items-container').innerHTML = `<p>Failed to load polls: ${error.message}</p>`;
        document.getElementById('page-info').textContent = 'Page 1 of 1';
        document.getElementById('prev-btn').disabled = true;
        document.getElementById('next-btn').disabled = true;
    }
});

// Function to render poll list with pagination
function renderPollList() {
    const pollItemsContainer = document.getElementById('poll-items-container');
    if (!pollItemsContainer) {
        console.error('poll-items-container not found in the DOM');
        return;
    }
    pollItemsContainer.innerHTML = ''; // Clear previous items

    // Calculate start and end indices for the current page
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedPolls = filteredPolls.slice(startIndex, endIndex); // Use filteredPolls

    // Render paginated poll items
    paginatedPolls.forEach(poll => {
        const pollItem = document.createElement('div');
        pollItem.classList.add('poll-item');
        pollItem.textContent = poll.name;
        if (poll.formInstanceId == selectedFormId) {
            pollItem.classList.add('selected');
        }
        pollItem.addEventListener('click', () => {
            document.querySelectorAll('.poll-item').forEach(item => item.classList.remove('selected'));
            pollItem.classList.add('selected');
            selectedFormId = poll.formInstanceId;
        });
        pollItemsContainer.appendChild(pollItem);
    });

    // Update pagination info and buttons
    const totalPages = Math.ceil(filteredPolls.length / itemsPerPage); // Use filteredPolls
    const pageInfo = document.getElementById('page-info');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');

    if (pageInfo && prevBtn && nextBtn) {
        pageInfo.textContent = `Page ${currentPage} of ${totalPages}`;
        prevBtn.disabled = currentPage === 1;
        nextBtn.disabled = currentPage === totalPages || totalPages === 0;
    }
}

// Pagination button handlers
function handlePrevClick() {
    if (currentPage > 1) {
        currentPage--;
        renderPollList();
    }
}

function handleNextClick() {
    const totalPages = Math.ceil(filteredPolls.length / itemsPerPage); // Use filteredPolls
    if (currentPage < totalPages) {
        currentPage++;
        renderPollList();
    }
}

// Function to filter polls based on search input
function filterPolls() {
    const searchInput = document.getElementById('search-input');
    const searchTerm = searchInput.value.toLowerCase().trim();

    // Filter the entire pollsData array
    filteredPolls = pollsData.filter(poll => 
        poll.name.toLowerCase().includes(searchTerm)
    );

    // Reset to first page after filtering
    currentPage = 1;

    // Re-render the list with filtered data
    renderPollList();
}

// Add event listener for search input
document.getElementById('search-input').addEventListener('input', filterPolls);

addFormLink.addEventListener('click', () => {
    addModal.style.display = 'flex';

    const addIframe = document.getElementById('add-form-iframe');
    if (addIframe) {
        addIframe.style.display = 'block';
    }
});


editFormLink.addEventListener('click', () => {
    const formInstanceId = localStorage.getItem('selectedPollId');
    if (formInstanceId) {
        const editUrl = `/group/control_panel/manage?p_p_id=com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet&p_p_lifecycle=0&_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_mvcRenderCommandName=%2Fadmin%2Fedit_form_instance&_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_formInstanceId=${formInstanceId}&_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet_redirect=%2Fgroup%2Fcontrol_panel%2Fmanage%3Fp_p_id%3Dcom_liferay_dynamic_data_mapping_form_web_portlet_DDMFormAdminPortlet%26p_p_lifecycle%3D0%26p_p_state%3Dmaximized%26p_p_mode%3Dview`;
        editFormIframe.src = editUrl;
        editModal.style.display = 'flex';

        const editIframe = document.getElementById('edit-form-iframe');
        if (editIframe) {
            editIframe.style.display = 'block';
        }
    }
});

editModalCloseBtn.addEventListener('click', () => {
    // Close the edit modal
    editModal.style.display = 'none';
    editFormIframe.src = '';

    // Refresh the iframe
    const iframe = document.getElementById('poll-iframe');
    iframe.src = iframe.src; // Refresh the iframe by setting its src to itself
});

closeBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        modal.style.display = 'none';
        addModal.style.display = 'none';
        addFormIframe.src = '';
    });
});

cancelBtn.addEventListener('click', () => {
    modal.style.display = 'none';
});


saveBtn.addEventListener('click', () => {
    iframe.src = `/web/forms/shared/-/form/${selectedFormId}`;
    localStorage.setItem('selectedPollId', selectedFormId);
    modal.style.display = 'none';
    checkPollSelection(); // Call checkPollSelection after saving
});

// Close modal if clicked outside
window.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
    if (event.target === addModal) {
        addModal.style.display = 'none';
    }
    if (event.target === editModal) {
        editModal.style.display = 'none';
    }
});

// Function to check poll selection
async function checkPollSelection() {
    try {
        loader.style.display = 'flex'; // Show loader
        surveyContainer.style.display = 'none'; // Ensure survey container is hidden
        const response = await fetch(`/o/polls/${selectedFormId}/records/by/${themeDisplay.getUserId()}?languageId=${Liferay.ThemeDisplay.getLanguageId().replace("_","-")} `, {
            headers: {
                'x-csrf-token': Liferay.authToken,
				'languageId': Liferay.ThemeDisplay.getLanguageId()
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
            pollBox.style.display = 'block'; // Show the poll box if no data is present
            loader.style.display = 'none'; // Hide loader
        }

        const data = await response.json();

        if (data.status === 'success' && data.data.length > 0) {
            pollBox.style.display = 'none'; // Hide the poll box if data is present
            fetchPollAnalytics(); // Fetch poll analytics data
        } else {
            pollBox.style.display = 'block'; // Show the poll box if no data is present
            loader.style.display = 'none'; // Hide loader
        }
    } catch (error) {
        pollBox.style.display = 'block'; // Show the poll box in case of error
        loader.style.display = 'none'; // Hide loader
    }
}

// Function to fetch poll analytics data
async function fetchPollAnalytics() {
    try {
        // Show the loader and ensure the survey table is hidden
        loader.style.display = 'flex';
        surveyContainer.style.display = 'none';

        const response = await fetch(`/o/polls/${selectedFormId}/analytics?languageId=${Liferay.ThemeDisplay.getLanguageId().replace("_","-")}`, {
            headers: {
                'x-csrf-token': Liferay.authToken,
				'languageId': Liferay.ThemeDisplay.getLanguageId()
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        if (data.status === 'success' && data.data) {
            updateSurveyContainer(data.data);
        } else {
            console.error('Error: Invalid response from server.');
            loader.style.display = 'none'; // Hide loader in case of error
        }
    } catch (error) {
        console.error('Error fetching poll analytics:', error);
        loader.style.display = 'none'; // Hide loader in case of error
    }
}

// Function to update survey container with dynamic data
function updateSurveyContainer(data) {
    surveyQuestion.textContent = data.question;
	
	surveyHeading.textContent = data.name || "Unnamed";


    const totalVotesCount = data.pollAnalyticsDTOS.reduce((sum, option) => sum + option.numberOfVotes, 0);
    totalVotes.textContent = `Total Votes: ${totalVotesCount}`;

    const resultsBody = surveyResultsTable.querySelector('tbody');
    if (resultsBody) {
        resultsBody.innerHTML = '';
    } else {
        const tbody = document.createElement('tbody');
        surveyResultsTable.appendChild(tbody);
    }

    data.pollAnalyticsDTOS.forEach(option => {
        const percentage = ((option.numberOfVotes / totalVotesCount) * 100).toFixed(2);
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${percentage}%</td>
            <td>${option.numberOfVotes}</td>
            <td>
                <div class="progress-bar-container">
                    <div class="progress-bar" style="width: ${percentage}%;"></div>
                </div>
            </td>
            <td>${option.optionName}</td>
        `;
        surveyResultsTable.querySelector('tbody').appendChild(row);
    });

    // Hide the loader and show the survey table
    loader.style.display = 'none';
    surveyContainer.style.display = 'block';
}

// Initial check on page load
//document.addEventListener('DOMContentLoaded', checkPollSelection);
checkPollSelection()

function setupSubmitMonitoring() {
    const iframe = document.getElementById('poll-iframe');
    if (iframe.contentDocument) {
        const submitBtn = iframe.contentDocument.getElementById('ddm-form-submit');
        if (submitBtn) {
            submitBtn.addEventListener('click', () => {
                console.log('Submit clicked - starting monitoring');

                loader.style.display = 'flex';
                const checkToast = setInterval(() => {
                    const toast = iframe.contentDocument.getElementById('ToastAlertContainer');
                    const validationError = iframe.contentDocument.querySelector('.form-feedback-item');
                    
                     if (validationError && validationError.offsetParent !== null) {
                        clearInterval(checkToast);
                        loader.style.display = 'none';
                    } else if (toast) {
                        const success = toast.querySelector('div.alert-success');
                        if (success) {
                            console.log('Success found - clearing interval');
                            clearInterval(checkToast);
                            checkPollSelection();
                            loader.style.display = 'none';
                        }
                    }
                }, 500);
                // Safety timeout
                setTimeout(() => {
                    clearInterval(checkToast);
                    loader.style.display = 'none';
                }, 30000);
            });
        }
    }
}

// Call this when iframe loads
iframe.addEventListener('load', () => {
    setTimeout(setupSubmitMonitoring, 1000);
});

document.addEventListener('DOMContentLoaded', () => {
    surveyContainer.style.display = 'none'; // Ensure survey container is hidden initially
    checkPollSelection();
});