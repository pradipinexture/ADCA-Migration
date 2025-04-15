<div class="adc-dxp-events-list-layout" id="main-content" role="main">
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-12">
			<div class="container-fluid mt-3">
				<div class="row">
					<div class="col-12">
						<div class="row">
							<div class="col-12 p-0 big-news">							
								<div class="top_info_nav d-none d-md-block">
									<div class="btn-group-vertical top_info_nav_list" role="group" data-toggle="buttons">
										<button onclick="toggle_visibility('column-1');" class="btn active" title="">
											<input type="radio" name="options" style="visibility:hidden" checked />
											<i class="customs-icons icon_highlights">&#xe804;</i>
											<span class="i-name resizable-text">Past</span>
										</button>
										<button onclick="toggle_visibility('column-2');" class="btn "title="">
											<input type="radio" name="options" style="visibility:hidden"/>
											<i class="customs-icons icon_news">&#xe808;</i>
											<span class="i-name resizable-text">Upcoming</span>
										</button>
									</div>
								</div>
							</div>
							<div class="col-md-12 portlet-column portlet-column-only submenuhomepage active" id="column-1">
								${processor.processColumn("column-1", "portlet-column-content portlet-column-content-only")}
							</div>
							<div class="col-md-12 portlet-column portlet-column-only submenuhomepage" id="column-2">
								${processor.processColumn("column-2", "portlet-column-content portlet-column-content-only")}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="portlet-layout row">
		<div class="col-md-12 portlet-column portlet-column-only" id="column-3">
			${processor.processColumn("column-3", "portlet-column-content portlet-column-content-only")}
		</div>
	</div>
</div>
