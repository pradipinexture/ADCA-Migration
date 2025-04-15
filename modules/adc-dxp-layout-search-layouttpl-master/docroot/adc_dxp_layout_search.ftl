<div class="adc-dxp-layout-search" id="main-content" role="main">
	<div class="portlet-layout row">
		<div class="container" >
			<div class="row">
				<div class="col-md-12 portlet-column" id="column-1">
					${processor.processColumn("column-1", "portlet-column-content")}
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 portlet-column" id="column-2">
					${processor.processColumn("column-2", "portlet-column-content")}
				</div>
			</div>
			
			<div class="row">
				<div class="col-md-6 portlet-column" id="column-3">
					${processor.processColumn("column-3", "portlet-column-content")}
				</div>
				<div class="col-md-6 portlet-column" id="column-4">
					${processor.processColumn("column-4", "portlet-column-content")}
				</div>
			</div>
		</div>		
	</div>

	<div class="portlet-layout row">
		<div class="container-fluid pt-5 pb-5 container-gold-20" >
			<div class="container">
				<div class="row">
					<div class="col-md-12 portlet-column portlet-column-only" id="column-5">
						<div class="d-flex justify-content-end results_header">
							${processor.processColumn("column-5", "portlet-column-content portlet-column-content-only")}
						</div>	
					</div>	
				</div>
				<div class="row">
					<div class="col-md-12 portlet-column portlet-column-only" id="column-6">
						${processor.processColumn("column-6", "portlet-column-content portlet-column-content-only")}
					</div>
				</div>
			</div>
		</div>
	</div>
	
</div>
