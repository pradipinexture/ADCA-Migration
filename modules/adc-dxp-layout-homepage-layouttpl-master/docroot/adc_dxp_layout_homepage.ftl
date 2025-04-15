<div class="adc-dxp-layout-homepage" id="main-content" role="main">
	<div class="portlet-layout portlet-highlights row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-1234">
			<div class="container-fluid mt-3">
				<div class="row">
					<#if themeDisplay??>
						<#assign highlights_text=themeDisplay.translate("homepage.submenuhomepage.highlights")
							news_text=themeDisplay.translate("homepage.submenuhomepage.news")
							events_text=themeDisplay.translate("homepage.submenuhomepage.events")
							documentation_text=themeDisplay.translate("homepage.submenuhomepage.documentation")
							application_text=themeDisplay.translate("homepage.submenuhomepage.application")
							application_link="/group/portal/applications"
							newspaper_text=themeDisplay.translate("homepage.submenuhomepage.newspaper")
							newspaper_link="/group/portal/newspaper" />
						<div class="top_info_nav d-none d-md-block">

							<div class="btn-group-vertical top_info_nav_list" role="group" data-toggle="buttons">
								<button onclick="toggle_visibility('column-1');" class="btn active" title="">
									<input type="radio" name="options" style="visibility:hidden" checked />
									<i class="customs-icons icon_highlights">&#xe804;</i>
									<span class="i-name resizable-text">${highlights_text}</span>
								</button>
								<button onclick="toggle_visibility('column-2');" class="btn " title="">
									<input type="radio" name="options" style="visibility:hidden" />
									<i class="customs-icons icon_news">&#xe808;</i>
									<span class="i-name resizable-text">${news_text}</span>
								</button>
								<button onclick="toggle_visibility('column-3');" class="btn " title="">
									<input type="radio" name="options" style="visibility:hidden" />
									<i class="customs-icons icon_events">&#xe802;</i>
									<span class="i-name resizable-text">${events_text}</span>
								</button>
								<button onclick="toggle_visibility('column-4');" class="btn " title="">
									<input type="radio" name="options" style="visibility:hidden" />
									<i class="customs-icons icon_documentation">&#xe801;</i>
									<span class="i-name resizable-text">${documentation_text}</span>
								</button>
								<button onclick="location.href='${application_link}'" class="btn " title="">
									<input type="radio" name="options" style="visibility:hidden" />
									<i class="fal fa-phone-laptop"></i>
									<span class="i-name resizable-text">${application_text}</span>
								</button>
								<button onclick="location.href='${newspaper_link}'" class="btn " title="">
									<input type="radio" name="options" style="visibility:hidden" />
									<i class="fal fa-newspaper"></i>
									<span class="i-name resizable-text">${newspaper_text}</span>
								</button>
							</div>

						</div>
						<div class="top_info_dropdown d-block d-md-none">
							<div class="form-group col-md-4">
								<select id="toggleVisibilitySelect" class="form-control"
									onchange="toggleVisibilitySelect()">
									<option value="column-1" selected>${highlights_text}</option>
									<option value="column-2">${news_text}</option>
									<option value="column-3">${events_text}</option>
									<option value="column-4">${documentation_text}</option>
									<option value='${application_link}'>${application_text}</option>
									<option value='${newspaper_link}'>${newspaper_text}</option>
								</select>
							</div>
						</div>
					</#if>

					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage active" id="column-1">
						${processor.processColumn("column-1", "portlet-column-content portlet-column-content-only")}
					</div>
					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage" id="column-2">
						${processor.processColumn("column-2", "portlet-column-content portlet-column-content-only")}
					</div>
					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage" id="column-3">
						${processor.processColumn("column-3", "portlet-column-content portlet-column-content-only")}
					</div>
					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage" id="column-4">
						${processor.processColumn("column-4", "portlet-column-content portlet-column-content-only")}
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-5">
			<div class="container-fluid container-gold">
				${processor.processColumn("column-5", "portlet-column-content portlet-column-content-only")}
			</div>
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-6">
			<div class="container-fluid pt-5 pb-5">
				${processor.processColumn("column-6", "portlet-column-content portlet-column-content-only")}
			</div>
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-7">
			<div class="container-fluid container-gold">
				${processor.processColumn("column-7", "portlet-column-content portlet-column-content-only")}
			</div>
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-8">
			${processor.processColumn("column-8", "portlet-column-content portlet-column-content-only")}
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-9">
			<div class="container-fluid container-gold-60">
				${processor.processColumn("column-9", "portlet-column-content portlet-column-content-only")}
			</div>
		</div>
	</div>

	<div class="bubble_more_wrapper">

		${processor.processColumn("column-10", "portlet-column-content portlet-column-content-only")}

		<div class="bubble_first bubble_polls">
			<a onClick="togglePoll()" title=""
				class="absolute_bubble pulse_animation d-flex justify-content-center align-items-center">
				<i class="fal fa-poll"></i>
			</a>
			<div class="bubble_background">

			</div>
		</div>
		<div class="clay-popover-top-right fade popover polls_class " id="poll-container">
			${processor.processColumn("column-11", "portlet-column-content portlet-column-content-only")}
		</div>
	</div>

</div>