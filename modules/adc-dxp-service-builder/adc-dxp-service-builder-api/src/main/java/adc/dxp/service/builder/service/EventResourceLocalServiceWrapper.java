/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link EventResourceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see EventResourceLocalService
 * @generated
 */
public class EventResourceLocalServiceWrapper
	implements EventResourceLocalService,
			   ServiceWrapper<EventResourceLocalService> {

	public EventResourceLocalServiceWrapper() {
		this(null);
	}

	public EventResourceLocalServiceWrapper(
		EventResourceLocalService eventResourceLocalService) {

		_eventResourceLocalService = eventResourceLocalService;
	}

	@Override
	public java.util.List<com.liferay.calendar.model.CalendarBooking>
		getCalendarBookingUpcoming() {

		return _eventResourceLocalService.getCalendarBookingUpcoming();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _eventResourceLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public EventResourceLocalService getWrappedService() {
		return _eventResourceLocalService;
	}

	@Override
	public void setWrappedService(
		EventResourceLocalService eventResourceLocalService) {

		_eventResourceLocalService = eventResourceLocalService;
	}

	private EventResourceLocalService _eventResourceLocalService;

}