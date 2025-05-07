<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${the_title} - ${company_name}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />
	<link rel="icon" type="image/png" href="/o/ad-customs-theme/images/favicon.png">

	<@liferay_util["include"] page=top_head_include />
	<link href="${css_folder}/owl.carousel.min.css" rel="stylesheet" type="text/css" />
	<link href="${css_folder}/lightgallery.css" rel="stylesheet">
	<link href="${css_folder}/jquery-ui.css" rel="stylesheet">
	<link href="${css_folder}/animate.css" rel="stylesheet">

	<script src="${javascript_folder}/lightGallery/lightgallery.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-video.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-pager.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-autoplay.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-fullscreen.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-zoom.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-hash.js"></script>
	<script src="${javascript_folder}/lightGallery/lg-share.js"></script>

</head>

<body class="${css_class}">

	<@liferay_ui["quick-access"] contentId="#main-content" />

	<@liferay_util["include"] page=body_top_include />


	<#assign belongsToGroup=false>
		<#list user.getUserGroups() as userGroup>
			<#if userGroup.getName()=="PortalContributors" || userGroup.getName()=="Contributors">
				<#assign belongsToGroup=true>
			</#if>
		</#list>

		<#if is_signed_in==true && (permissionChecker.isOmniadmin()==true || belongsToGroup==true)>
			<@liferay.control_menu />
		</#if>

		<div class="container-fluid pt-0 px-0 min-vh-100 ${(is_signed_in == true && (permissionChecker.isOmniadmin() == true || belongsToGroup == true))?then('loggedIn', '')} ${(is_signed_in == true && !permissionChecker.isOmniadmin() == true && !belongsToGroup )?then('viewerAccess', '')}"
			id="wrapper">
			<header id="banner" role="banner">


				<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
					<div class="container">
						<button class="navbar-toggler" type="button" onclick="toggleSideBar()"
							aria-label="Toggle sidebar">
							<span class="navbar-toggler-icon"></span>
						</button>
						<div class="collapse navbar-collapse justify-content-start" id="navbarNav">
							<ul class="navbar-nav text-right">

								<li class="nav-item mr-4">
									<@liferay.user_personal_bar />
								</li>
								<@liferay.languages />
							</ul>
						</div>
						<a class="navbar-brand" href="/group/portal/home"><img src="/o/ad-customs-theme/images/logo.png"
								alt="logo" width="180" /></a>
						<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
							aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
							<span class="navbar-toggler-icon"></span>
						</button>

					</div>
				</nav>
			</header>

			<section id="content">

				<div class="d-flex justify-content-between middle-content">
					<div class="mini-sidebar">
						<@liferay_portlet["runtime"] instanceId="quicklinksmenu123"
							portletName="com_liferay_site_navigation_menu_web_portlet_SiteNavigationMenuPortlet" />
					</div>

					<div class="sidebar main-sidebar sidebar-open">

						<@liferay_portlet["runtime"] instanceId="pg6lUoJHP0mZ"
							portletName="com_liferay_site_navigation_menu_web_portlet_SiteNavigationMenuPortlet" />
					</div>

					<div class="polls-popup dl-popup card-shadowed collapse" id="polls-container">
						<@liferay_portlet["runtime"] instanceId="lqPxc8HV1s6G"
							portletName="com_liferay_polls_web_portlet_PollsDisplayPortlet" />
					</div>


					<div class="modal d-none" id="satisfaction-modal" tabindex="-1" aria-labelledby="exampleModalLabel"
						aria-hidden="true">

						<div class="modal-dialog modal-dialog-centered modal-md modal-center">
							<div class="modal-content">
								<div class="modal-header">
									<h5 class="modal-title" id="exampleModalLabel">${surveyHeader}</h5>
									<button type="button" class="btn close-modal" data-bs-dismiss="modal"
										aria-label="Close"><i class="icon-remove"></i></button>
								</div>
								<div class="modal-body">
									<form id="feedbackForm">
										<div class="mb-3">
											<label class="form-label">${surveySatisfactionQuestion}</label>
											<div class="form-check text-center mt-5">
												<label class="radio-option">
													<input class="form-check-input" type="radio"
														name="satisfactionRating" value="Happy" checked>
													<img src="/o/ad-customs-theme/images/feedback/happy.png" alt="Happy"
														width="70">
													<p class="small"> ${happylbl}</p>
												</label>
												<label class="radio-option">
													<input class="form-check-input" type="radio"
														name="satisfactionRating" value="Neutral">
													<img src="/o/ad-customs-theme/images/feedback/neutral.png"
														alt="Neutral" width="70">
													<p class="small">${neutrallbl}</p>
												</label>
												<label class="radio-option">
													<input class="form-check-input" type="radio"
														name="satisfactionRating" value="Sad">
													<img src="/o/ad-customs-theme/images/feedback/sad.png" alt="Sad"
														width="70">
													<p class="small">${sadlbl}</p>
												</label>
											</div>
										</div>
										<div class="mb-3" id="messageField" style="display:none;">
											<label for="messageInput" class="form-label">${surveyFeedback}</label>
											<textarea class="form-control" id="messageInput" name="message"
												rows="4"></textarea>
										</div>

									</form>
									<div id="submitted-message" class="pb-3 d-none">
										<img src="/o/ad-customs-theme/images/feedback/completed.gif" height="100">
										<h4> ${thankslbl} </h4>
										<p>${submittedlbl} </p>
									</div>
								</div>
								<div class="modal-footer d-flex justify-content-end">

									<button type="button" class="btn btn-secondary-outline" id="submitBtn">
										<@liferay.language key='submit' />
									</button>
								</div>
							</div>
						</div>
					</div>
					<div class="floating-btns">

						<button class="btn btn-primary-filled rounded-circle satisfaction-modal-btn p-0" type="button">
							<img src="/o/ad-customs-theme/images/feedback/satisfaction-icon.svg" height="30" width="30"
								alt="" />

						</button>

					</div>

					<div class="container main-container mt-5">

						<#if selectable>
							<@liferay_util["include"] page=content_include />
							<#else>
								${portletDisplay.recycle()}

								${portletDisplay.setTitle(the_title)}

								<@liferay_theme["wrap-portlet"] page="portlet.ftl">
									<@liferay_util["include"] page=content_include />
									</@>
						</#if>

					</div>
				</div>

			</section>

			<footer id="footer" class="text-center py-4 bg-white border-top mt-6">
				<ul
					class="dl-list social list-unstyled d-flex social-media-links align-items-center justify-content-center mb-0 ">
					<li class="nav-item">
						<a class="nav-link" href="https://www.facebook.com/abudhabicustoms" target="_blank">
							<img height="20" src="/o/ad-customs-theme/images/social-media-icons/facebook.svg"></a>
					</li>
					<li class="nav-item"><a class="nav-link" href="https://www.instagram.com/abudhabicustoms/"
							target="_blank">
							<img height="20" src="/o/ad-customs-theme/images/social-media-icons/instagram.svg"> </a>
					</li>

					<li class="nav-item"><a class="nav-link" href="https://twitter.com/AbuDhabiCustoms" target="_blank">
							<img height="20" src="/o/ad-customs-theme/images/social-media-icons/twitter.svg"> </a></li>
					<li class="nav-item"><a class="nav-link"
							href="https://www.youtube.com/channel/UCBOvdWoR8htW87lBeESZp3A" target="_blank">
							<img height="25" src="/o/ad-customs-theme/images/social-media-icons/youtube.svg"></a></li>
					<li class="nav-item"><a class="nav-link" href="https://www.linkedin.com/company/abu-dhabi-customs/"
							target="_blank"> <img height="20"
								src="/o/ad-customs-theme/images/social-media-icons/linkedin.svg"></a></li>
				</ul>

			</footer>
		</div>

		<@liferay_util["include"] page=body_bottom_include />

		<@liferay_util["include"] page=bottom_include />

		<!-- inject:js -->

		<script src="${javascript_folder}/index.js"></script>
		<script src="${javascript_folder}/owl.carousel.min.js"></script>
		<script src="${javascript_folder}/WOW.js"></script>
		<script src="${javascript_folder}/jqurery-ui.js"></script>
		<script src="${javascript_folder}/feedback.js"></script>

		<!-- endinject -->

</body>

</html>