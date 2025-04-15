/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service.impl;

import adc.dxp.service.builder.service.base.EventResourceLocalServiceBaseImpl;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.CalendarLocalServiceUtil;
import com.liferay.portal.aop.AopService;

import com.liferay.portal.kernel.dao.orm.Criterion;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import org.osgi.service.component.annotations.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=adc.dxp.service.builder.model.EventResource",
	service = AopService.class
)
public class EventResourceLocalServiceImpl
	extends EventResourceLocalServiceBaseImpl {
	public EventResourceLocalServiceImpl() {
	}

	public List<CalendarBooking> getCalendarBookingUpcoming() {
		Date startTime = new Date();
		Date endTime = null;
		DynamicQuery bookingsDQ = CalendarBookingLocalServiceUtil.dynamicQuery();
		if (startTime != null) {
			Criterion startDateCriterion = RestrictionsFactoryUtil.ge("startTime", startTime.getTime());
			bookingsDQ.add(startDateCriterion);
		}

		if (endTime != null) {
			Criterion endDateCriterion = RestrictionsFactoryUtil.le("startTime", endTime.getTime());
			bookingsDQ.add(endDateCriterion);
		}

		List<Long> calendarsIds = Arrays.asList(119537L, 119535L, 119534L);
		Criterion calendarsCriterion = RestrictionsFactoryUtil.in("calendarId", calendarsIds);
		bookingsDQ.add(calendarsCriterion);
		List<CalendarBooking> events = CalendarLocalServiceUtil.dynamicQuery(bookingsDQ, -1, -1);
		return events;
	}
}