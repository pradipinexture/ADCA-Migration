<div class="adc-dxp-event-all-layout" id="main-content" role="main">
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-1">
			${processor.processColumn("column-1", "portlet-column-content portlet-column-content-only")}
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-2">
			${processor.processColumn("column-2", "portlet-column-content portlet-column-content-only")}
		</div>
	</div>
	<!--<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-3">
			<div class="container">
				${processor.processColumn("column-3", "portlet-column-content portlet-column-content-only")}
			</div>
		</div>
	</div>-->
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-1234">
			<div class="container-fluid mt-3">
				<div class="row">
					<div class="container">
						<div class="row">
							<div class="col-12 d-flex justify-content-end">
								<div class="btn-group btn-group-toggle" role="group" data-toggle="buttons">

									<#if themeDisplay??>
										<#assign events_all_text = themeDisplay.translate("layout-events-search-all") 
												events_calendar_text = themeDisplay.translate("layout-events-search-calendar")  
											/>	
										<button class="btn btn-secondary active"
											onclick="toggle_visibility('column-4');">
												<input style="visibility:hidden" checked
												type="radio"
												name="all"
												value="all"
												autoComplete="off"
												/>
												${events_all_text}
										</button>
										<button class="btn btn-secondary "
											onclick="toggle_visibility('column-5');">
												<input style="visibility:hidden"
												type="radio"
												name="calendar"
												value="calendar"
												autoComplete="off"
												/>
												${events_calendar_text}
										</button>

									</#if>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage active" id="column-4">
						${processor.processColumn("column-4", "portlet-column-content portlet-column-content-only")}
					</div>

					<div class="col-md-12 portlet-column portlet-column-only submenuhomepage " id="column-5">
						<div class="container">
							${processor.processColumn("column-5", "portlet-column-content portlet-column-content-only")}
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
