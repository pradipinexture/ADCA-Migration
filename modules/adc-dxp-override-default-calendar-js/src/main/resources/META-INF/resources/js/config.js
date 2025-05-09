; (function () {
    AUI().applyConfig({
        groups: {
            CalendarWebJsOverride: { // Please make sure to give Different Group Name.Here, We have given group name as CalendarWebJsOverride to override calendar-web module's javascript file.
                base: MODULE_PATH + '/js/',
                combine: Liferay.AUI.getCombine(),
                filter: Liferay.AUI.getFilterConfig(),
                modules: {
                    'liferay-scheduler-event-recorder-override': { //Override Module Name
                        path: 'custom-scheduler.js', // Override Javascript File Name
                        condition: {
                            name: 'liferay-scheduler-event-recorder-override', // Override Module Name
                            trigger: 'liferay-scheduler-event-recorder', when: 'instead' // Give Original Module Name in trigger
                        },
                        requires: ['async-queue', 'aui-datatype', 'aui-scheduler', 'dd-plugin', 'liferay-calendar-message-util',
                            'liferay-calendar-recurrence-converter', 'liferay-calendar-recurrence-util', 'liferay-calendar-util', 'liferay-node', 'liferay-store',
                            'promise', 'resize-plugin']
                    },
                }, root: MODULE_PATH + '/js/'
            },
        }
    });
})();