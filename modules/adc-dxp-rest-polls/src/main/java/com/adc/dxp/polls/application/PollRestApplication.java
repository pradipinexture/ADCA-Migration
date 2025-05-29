package com.adc.dxp.polls.application;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.adc.dxp.polls.application.dto.FormRecordDTO;
import com.adc.dxp.polls.application.dto.PollAnalyticsDTO;
import com.adc.dxp.polls.application.dto.PollDTO;
import com.adc.dxp.polls.application.dto.ResponseDTO;
import com.adc.dxp.polls.application.service.PollService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author root329
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/polls",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Greetings.Rest"
	},
	service = Application.class
)
public class PollRestApplication extends Application {

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@Reference
	private PollService pollService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPolls(@QueryParam("groupId") long groupId) {
		List<PollDTO> polls = pollService.getPolls(groupId);

		if (polls.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST).entity(
					new ResponseDTO<>("error", "Polls not found for the given group ID.", Collections.emptyList(), 0)
			).build();
		}

		return Response.ok(
				new ResponseDTO<>("success", "Polls fetched successfully.", polls, polls.size())
		).build();
	}

	@GET
	@Path("/{pollId}/records/by/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormRecords(
			@PathParam("pollId") long formId,
			@PathParam("userId") long userId) {

		List<FormRecordDTO> records = pollService.getFormRecords(formId, userId);

		if (records.isEmpty()) {
			ResponseDTO<List<FormRecordDTO>> response = new ResponseDTO<>(
					"error",
					"No records found for this user.",
					Collections.emptyList(),
					0
			);
			return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
		}

		ResponseDTO<List<FormRecordDTO>> response = new ResponseDTO<>(
				"success",
				"User form records fetched successfully.",
				records,
				records.size()
		);
		return Response.ok(response).build();
	}

	@GET
	@Path("/{pollId}/analytics")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPollAnalytics(@PathParam("pollId") long pollId) {
		PollDTO pollDTO = pollService.getPollAnalytics(pollId);

		if (pollDTO == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseDTO<>(
					"error", "No question or no votes submitted.", Collections.emptyList(), 0)
			).build();
		}

		return Response.ok(
				new ResponseDTO<>("success", "Poll analytics generated successfully.", pollDTO, 0)
		).build();
	}

}