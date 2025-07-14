Liferay.on('allPortletsReady',function() {

  $('.empView').addClass('d-none');
  $('.chart-container').addClass('d-none');
  $('.loader-container').addClass('d-none');
  $(document).off("click", ".more-details");
  $(document).off("click", ".toggleParent");


  var tempEmpArray = [];
  var pageNum = 1;
  var pageSize = 12;
  var lastPage = 0;
  var selectedSection = "";
  var sectionId = "";
  var languageId = Liferay.ThemeDisplay.getLanguageId();
  var morelbl = languageId == "en_US" ? "More" : "المزيد";
  let departments = {};

  orgJson = {
      BOD: {
          lvl1: {
              name: "General Director's Office Sector",
              nameAr: "قطاع مكتب المدير العام",
              subordinate: [
                  {
                      subName: "General Director's Office Sector",
                      divisionID: "300000001664551",
                      subSections: [],
                  },
                  {
                    subName:"Executive Office of the General Director’s Office Sector",
                    divisionID:'1234321',
                    subSections:[
                      {
                        subSectionName: "Internal Audit Office",
                        sectionID: "300000001664477",
                        subSections: [],
                    },
                      {
                        subSectionName: "Director General Office",
                        sectionID: "300000001664552",
                        subSections: [],
                      },
                      {
                      subSectionName: "Environment, Health & Safety Section",
                      sectionID: "300000001664512",
                      subSections: [],
                      },
                    ],
                  },
                  
                  {
                      subName: "Information Security Unit",
                      divisionID: "300000001664612",
                      subSections: [],
                  },
                  {
                      subName: "Legal Affairs Division",
                      divisionID: "300000001664597",
                      subSections: [
                          {
                              subSectionName: "Consultancy & Legal Studies Section",
                              sectionID: "300000001664532",
                          },
                      ],
                  },
                  {
                      subName: "Development & Strategic Planning Divion",
                      divisionID: "300000001664492",
                      subSections: [
                          {
                              subSectionName: "Corporate Performance & Excellence Section",
                              sectionID: "300000001664562",
                          },
                          {
                              subSectionName: "Studies and Planning Section",
                              sectionID: "300000001664389",
                          },
                          {
                              subSectionName: "Data Management Section",
                              sectionID: "300000001664367",
                          },
                          {
                              subSectionName: "Revenue Development Section",
                              sectionID: "300000001664359",
                          },
                      ],
                  },
                
                  {
                      subName: "Project Management Division",
                      divisionID: "300000001664472",
                      subSections: [],
                  },
                  {
                      subName: "Corporate Communications & Customer Happiness Division",
                      divisionID: "300000001664527",
                      subSections: [
                          {
                              subSectionName: "Communication & Corporate Marketing Section",
                              sectionID: "300000001664329",
                          },
                          {
                              subSectionName: "Public Relations & Customer Happiness Section",
                              sectionID: "300000001664592",
                          },
                      ],
                  },
              ],
          },
          lvl2: {
              name: "sectors",
              sectors: [
                  {
                      sectorName: "Technical Affairs Sector",
                      sectorId: "300000001664337",
                      departments: [
                          {
                              depName: "Risk Management Divsion",
                              divisionID: "300000001664417",
                              depSections: [
                                  {
                                      sectionName: "Customs Intelligence Section",
                                      sectionID: "300000001664457",
                                  },
                                  {
                                      sectionName: "Risk Criteria Section",
                                      sectionID: "300000001664347",
                                  },
                              ],
                          },
                          {
                              depName: "Customer Affairs Divsion",
                              divisionID: "300000001664607",
                              depSections: [
                                  {
                                      sectionName: "Customs Licensing Section",
                                      sectionID: "300000001664399",
                                  },
                                  {
                                      sectionName: "Tariff, Value and Origin Section",
                                      sectionID: "300000001664502",
                                  },
                                  {
                                      sectionName:
                                          "Hazardous Materials, Prohibitions and Restrictions Sectiion",
                                      sectionID: "300000001664369",
                                  },
                                  {
                                      sectionName:
                                          "Faciliation and Compliance Section - Exemptions",
                                      sectionID: "300000001664617",
                                  },
                                  {
                                      sectionName: "Customs Audit Section",
                                      sectionID: "300000001664577",
                                  },
                              ],
                          },
                          {
                              depName: "Policies & Governance Division",
                              divisionID: "300000001664537",
                              depSections: [
                                  {
                                      sectionName:
                                          "International Agreements & Intellectual Property Section",
                                      sectionID: "300000001664572",
                                  },
                                  {
                                      sectionName: "Customs Policies & Procedures Section",
                                      sectionID: "300000001664622",
                                  },
                              ],
                          },
                      ],
                  },
                
                  {
                      sectorName: "Support Service Sector",
                      sectorId: "300000001664627",
                      departments: [
                          {
                              depName: "Human Resources Division",
                              divisionID: "300000001664409",
                              depSections: [
                                  {
                                      sectionName: "Training & Performance Management Section",
                                      sectionID: "300000001664309",
                                  },
                                  {
                                      sectionName: "Employee Relations Section",
                                      sectionID: "300000001664317",
                                  },
                                  {
                                      sectionName: "Organizational Development Section",
                                      sectionID: "300000001664377",
                                  },
                                  {
                                      sectionName: "Recruitment Section",
                                      sectionID: "300000001664522",
                                  },
                              ],
                          },
                          {
                              depName: "Information Technology Division",
                              divisionID: "300000001664482",
                              depSections: [
                                  {
                                      sectionName: "Operations and Technical Support Section",
                                      sectionID: "300000001664307",
                                  },
                                  {
                                      sectionName: "Application Development Section",
                                      sectionID: "300000001664319",
                                  },
                                  {
                                      sectionName: "Networks Section",
                                      sectionID: "300000001664557",
                                  },
                              ],
                          },
                          {
                              depName: "Services Division",
                              divisionID: "300000038999874",
                              depSections: [
                                  {
                                      sectionName: "Buildings and Maintenance Section",
                                      sectionID: "300000001664547",
                                  },
                                  {
                                      sectionName: "Services and Archiving Section",
                                      sectionID: "300000001664497",
                                  },
                                  {
                                      sectionName: "Purchasing and Contracts Section",
                                      sectionID: "300000001664339",
                                  },
                              ],
                          },
                          {
                              depName: "Financial Affairs Division",
                              divisionID: "300000001664427",
                              depSections: [
                                  {
                                      sectionName: "Accounts and Revenue Section",
                                      sectionID: "300000001664507",
                                  },
                                  {
                                      sectionName: "Budget Section",
                                      sectionID: "300000001664602",
                                  },
                                  {
                                      sectionName: "Payments Section",
                                      sectionID: "300000001664437",
                                  },
                              ],
                          },
                      ],
                  },
                  {
                      sectorName: "Operations Sector",
                      sectorId: "300000001664419",
                      departments: [
                          {
                              depName: "Operations Sector Division",
                              divisionID: "300000001664419",
                              depSections: [
                                  {
                                      sectionName: "Al Ghuwaifat Customs Center",
                                      sectionID: "300000001664517",
                                  },
                                  {
                                      sectionName: "Al Heeli & Mudeef Customs Center",
                                      sectionID: "300000001664582",
                                  },
                                  {
                                      sectionName: "Khalifa Port Customs Center",
                                      sectionID: "300000001664447",
                                  },
                                  {
                                      sectionName: "Control Section",
                                      sectionID: "300000001664379",
                                  },
                                  {
                                      sectionName: "Mazied Customs Center",
                                      sectionID: "300000001664429",
                                  },
                                  {
                                      sectionName: "khatem Al Shakilah Customs Center",
                                      sectionID: "300000001664542",
                                  },
                                  {
                                      sectionName: "Abu Dhabi Aiport Customs Center",
                                      sectionID: "300000001664487",
                                  },
                                  {
                                      sectionName: "Operations - Passengers Section",
                                      sectionID: "300000043303188",
                                  },
                                  {
                                      sectionName: "Operations - Cargo & Post Section",
                                      sectionID: "300000038999559",
                                  },
                              ],
                          },
                      ],
                  },
              ],
          },
      },
  };


  renderChart();

  function getDepartments(id) {

      let departmentsList = departments?.list;

      return departmentsList.find(x => x.departmentId == id)
  }



  function getAllDepartments() {

      let URL = `https://hudhoor-api.adcustoms.gov.ae/dxb/1/1000`;
      let res= {
  "list": [
      {
          "id": 1,
          "departmentId": 300000001664497,
          "title": "Services and Archiving Section",
          "titleAr": "قسم الخدمات والأرشفة",
          "parent": {
              "id": 396,
              "title": "Services  Division",
              "titleAr": "إدارة الخدمات"
          }
      },
      {
          "id": 2,
          "departmentId": 300000001664542,
          "title": "Khatem Al Shakilah Customs Center",
          "titleAr": "مركز جمرك خطم الشكلة",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 3,
          "departmentId": 300000001664317,
          "title": "Employee Relations Section",
          "titleAr": "قسم علاقات الموظفين",
          "parent": {
              "id": 361,
              "title": "Human Resources Division",
              "titleAr": "إدارة الموارد البشرية"
          }
      },
      {
          "id": 4,
          "departmentId": 300000001664377,
          "title": "Organizational Development Section",
          "titleAr": "قسم التطوير المؤسسي",
          "parent": {
              "id": 361,
              "title": "Human Resources Division",
              "titleAr": "إدارة الموارد البشرية"
          }
      },
      {
          "id": 5,
          "departmentId": 300000001664309,
          "title": "Training & Performance Management Section",
          "titleAr": "قسم التدريب وإدارة الأداء",
          "parent": {
              "id": 361,
              "title": "Human Resources Division",
              "titleAr": "إدارة الموارد البشرية"
          }
      },
      {
          "id": 6,
          "departmentId": 300000001664389,
          "title": "Studies and Planning Section",
          "titleAr": "قسم الدراسات والتخطيط",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير والتخطيط الاستراتيجي"
          }
      },
      {
          "id": 7,
          "departmentId": 300000001664379,
          "title": "Control Section",
          "titleAr": "قسم التحكم والسيطرة",
          "parent": {
              "id": 278,
              "title": "Support Services Sector",
              "titleAr": "قطاع الخدمات المساندة"
          }
      },
      {
          "id": 8,
          "departmentId": 300000001664419,
          "title": "Operations Sector",
          "titleAr": "قطاع العمليات",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 9,
          "departmentId": 300000001664592,
          "title": "Public Relations & Customer Happiness Section",
          "titleAr": "قسم العلاقات العامة وأسعاد المتعاملين",
          "parent": {
              "id": 399,
              "title": "Corporate Communications & Customer Happiness Division",
              "titleAr": "إدارة الاتصال المؤسسي وسعادة المتعاملين"
          }
      },
      {
          "id": 249,
          "departmentId": 300000001664482,
          "title": "Information Technology Division",
          "titleAr": "إدارة تقنية المعلومات",
          "parent": {
              "id": 278,
              "title": "Support Services Sector",
              "titleAr": "قطاع الخدمات المساندة"
          }
      },
      {
          "id": 250,
          "departmentId": 300000001664492,
          "title": "Development & Strategic Planning Division",
          "titleAr": "إدارة التطوير والتخطيط الاستراتيجي",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 251,
          "departmentId": 300000001664439,
          "title": "Risk Managemen Division",
          "titleAr": "إدارة إدارة المخاطر",
          "parent": {
              "id": 461,
              "title": "Technical Affairs Sector",
              "titleAr": "قطاع الشؤون الفنية"
          }
      },
      {
          "id": 252,
          "departmentId": 300000001664547,
          "title": "Buildings and Maintenance Section",
          "titleAr": "قسم المباني والصيانة",
          "parent": {
              "id": 396,
              "title": "Services  Division",
              "titleAr": "إدارة الخدمات"
          }
      },
      {
          "id": 253,
          "departmentId": 300000001664407,
          "title": "Investigations & Litigation Section",
          "titleAr": "قسم التحقيقات والدعاوي القضائي",
          "parent": {
              "id": 287,
              "title": "Legal Affairs Division",
              "titleAr": "إدارة الشؤون القانونية"
          }
      },
      {
          "id": 254,
          "departmentId": 300000001664447,
          "title": "Khalifa Port Customs Center",
          "titleAr": "مركز جمرك ميناء خليفة",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 255,
          "departmentId": 300000038999754,
          "title": "INTERNAL ACCOUNTS SECTION",
          "titleAr": "INTERNAL ACCOUNTS SECTION",
          "parent": null
      },
      {
          "id": 256,
          "departmentId": 300000038999792,
          "title": "Consultancy and Lega Studies Section",
          "titleAr": "Consultancy and Lega Studies Section",
          "parent": null
      },
      {
          "id": 257,
          "departmentId": 300000038999673,
          "title": "CUSTOM PROCEDURES SECTION ( INDUSTRIAL POST)",
          "titleAr": "CUSTOM PROCEDURES SECTION ( INDUSTRIAL POST)",
          "parent": null
      },
      {
          "id": 258,
          "departmentId": 300000038999960,
          "title": "EXECUTIVE MANAGEMENT OFFICE",
          "titleAr": "EXECUTIVE MANAGEMENT OFFICE",
          "parent": null
      },
      {
          "id": 259,
          "departmentId": 300000038999573,
          "title": "CONTROBUTIONS & SUPPORT SECTION",
          "titleAr": "CONTROBUTIONS & SUPPORT SECTION",
          "parent": null
      },
      {
          "id": 260,
          "departmentId": 300000043303231,
          "title": "CUSTOM CENTER - KHATEM A SHAKILAH , MUDEEF & AL HEELI",
          "titleAr": "CUSTOM CENTER - KHATEM A SHAKILAH , MUDEEF & AL HEELI",
          "parent": null
      },
      {
          "id": 261,
          "departmentId": 300000043303066,
          "title": "SERVICE STRATEGY AND PLANNING SECTION",
          "titleAr": "SERVICE STRATEGY AND PLANNING SECTION",
          "parent": null
      },
      {
          "id": 262,
          "departmentId": 300000043303124,
          "title": "Recruitment ?Section",
          "titleAr": "Recruitment ٍSection",
          "parent": null
      },
      {
          "id": 263,
          "departmentId": 300000038999740,
          "title": "HOUSING SECTION",
          "titleAr": "HOUSING SECTION",
          "parent": null
      },
      {
          "id": 264,
          "departmentId": 300000038999702,
          "title": "CUSTOM CENTER - AD POST",
          "titleAr": "CUSTOM CENTER - AD POST",
          "parent": null
      },
      {
          "id": 265,
          "departmentId": 300000038999794,
          "title": "RELEASING SECTION - ZAYED PORT",
          "titleAr": "RELEASING SECTION - ZAYED PORT",
          "parent": null
      },
      {
          "id": 266,
          "departmentId": 300000043303285,
          "title": "ADMINISTRATIVE AFFAIRS SECTION - ZAYED PORT",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION - ZAYED PORT",
          "parent": null
      },
      {
          "id": 267,
          "departmentId": 300000038999900,
          "title": "CLASSIFICATION, EVALUATION, ORIGIN SECTION",
          "titleAr": "CLASSIFICATION, EVALUATION, ORIGIN SECTION",
          "parent": null
      },
      {
          "id": 268,
          "departmentId": 300000038999901,
          "title": "GENERAL ACCOUNTS SECTION - SS",
          "titleAr": "GENERAL ACCOUNTS SECTION - SS",
          "parent": null
      },
      {
          "id": 269,
          "departmentId": 300000038999633,
          "title": "CUSTOMS PROCEDURES SECTION - CENTRAL POST",
          "titleAr": "CUSTOMS PROCEDURES SECTION - CENTRAL POST",
          "parent": null
      },
      {
          "id": 270,
          "departmentId": 300000043303139,
          "title": "FINANCIAL AFFAIRS SECTION -KHALIFA PORT",
          "titleAr": "FINANCIAL AFFAIRS SECTION -KHALIFA PORT",
          "parent": null
      },
      {
          "id": 271,
          "departmentId": 300000038999647,
          "title": "CUSTOMS REVENUES AND COLLECTIONS SECTION",
          "titleAr": "CUSTOMS REVENUES AND COLLECTIONS SECTION",
          "parent": null
      },
      {
          "id": 272,
          "departmentId": 300000043303073,
          "title": "Organizational Development ?Section",
          "titleAr": "Organizational Development ?Section",
          "parent": null
      },
      {
          "id": 273,
          "departmentId": 300000038999945,
          "title": "INTERNAL ACCOUNTS DIVISION",
          "titleAr": "INTERNAL ACCOUNTS DIVISION",
          "parent": null
      },
      {
          "id": 274,
          "departmentId": 300000001664472,
          "title": "Project Management Division",
          "titleAr": "إدارة المشاريع",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 275,
          "departmentId": 300000001664517,
          "title": "Al Ghuwaifat  Customs Center",
          "titleAr": "مركز جمرك الغويفات",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 276,
          "departmentId": 300000001664582,
          "title": "Al Heeli & Mudeef Customs Center",
          "titleAr": "مركز جمرك الهيلي والمضيف",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 277,
          "departmentId": 300000001664607,
          "title": "Customs Affairs Division",
          "titleAr": "إدارة الشؤون الجمركية",
          "parent": {
              "id": 461,
              "title": "Technical Affairs Sector",
              "titleAr": "قطاع الشؤون الفنية"
          }
      },
      {
          "id": 278,
          "departmentId": 300000001664627,
          "title": "Support Services Sector",
          "titleAr": "قطاع الخدمات المساندة",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 279,
          "departmentId": 300000001664307,
          "title": "Operations and Technical Support Section",
          "titleAr": "قسم العمليات والدعم الفني",
          "parent": {
              "id": 249,
              "title": "Information Technology Division",
              "titleAr": "إدارة تقنية المعلومات"
          }
      },
      {
          "id": 280,
          "departmentId": 300000001664329,
          "title": "Communications & Corporate Marketing Section",
          "titleAr": "قسم الاتصال والتسويق المؤسسي",
          "parent": {
              "id": 399,
              "title": "Corporate Communications & Customer Happiness Division",
              "titleAr": "إدارة الاتصال المؤسسي وسعادة المتعاملين"
          }
      },
      {
          "id": 281,
          "departmentId": 300000001664397,
          "title": "Int'l Cooperation Section",
          "titleAr": "قسم التعاون الدولي",
          "parent": {
              "id": 400,
              "title": "Policies & Governance Division",
              "titleAr": "إدارة السياسات والحوكمة"
          }
      },
      {
          "id": 282,
          "departmentId": 300000001664487,
          "title": "ABU DHABI AIRPORT CUSTOMS CENTER",
          "titleAr": "مركز جمرك مطار أبوظبي",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 283,
          "departmentId": 300000001664359,
          "title": "Revenue Development Section",
          "titleAr": "قسم تنمية الإيرادات",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 284,
          "departmentId": 300000001664359,
          "title": "Revenue Development Section",
          "titleAr": "قسم تنمية الإيرادات",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 285,
          "departmentId": 300000001664367,
          "title": "Data Management Section",
          "titleAr": "قسم إدارة البيانات",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 286,
          "departmentId": 300000001664369,
          "title": "Hazardous Materials , Prohibitions and Restrictions Section",
          "titleAr": "قسم المواد الخطرة والمنع والقيد",
          "parent": {
              "id": 277,
              "title": "Customs Affairs Division",
              "titleAr": "إدارة الشؤون الجمركية"
          }
      },
      {
          "id": 287,
          "departmentId": 300000001664597,
          "title": "Legal Affairs Division",
          "titleAr": "إدارة الشؤون القانونية",
          "parent": {
              "id": 461,
              "title": "Technical Affairs Sector",
              "titleAr": "قطاع الشؤون الفنية"
          }
      },
      {
          "id": 288,
          "departmentId": 300000043303216,
          "title": "CUSTOMS CLEARANCE SECTION -KHALIFA PORT",
          "titleAr": "CUSTOMS CLEARANCE SECTION -KHALIFA PORT",
          "parent": null
      },
      {
          "id": 289,
          "departmentId": 300000038999629,
          "title": "ADMINISTRATIVE AFFAIRS -ABU DHABI POST",
          "titleAr": "ADMINISTRATIVE AFFAIRS -ABU DHABI POST",
          "parent": null
      },
      {
          "id": 290,
          "departmentId": 300000043303156,
          "title": "VALUATION & CLASSIFICATION SECTION",
          "titleAr": "VALUATION & CLASSIFICATION SECTION",
          "parent": null
      },
      {
          "id": 291,
          "departmentId": 300000038999664,
          "title": "MUSSAFAH PORT OPERATIONS",
          "titleAr": "MUSSAFAH PORT OPERATIONS",
          "parent": null
      },
      {
          "id": 292,
          "departmentId": 300000038999645,
          "title": "INSURANCE SECTION",
          "titleAr": "INSURANCE SECTION",
          "parent": null
      },
      {
          "id": 293,
          "departmentId": 300000043303244,
          "title": "ADMINISTRATION AFFAIRS SECTION - AL AIN AIRPORT",
          "titleAr": "ADMINISTRATION AFFAIRS SECTION - AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 294,
          "departmentId": 300000043303203,
          "title": "PUBLIC RELATIONS SECTION",
          "titleAr": "PUBLIC RELATIONS SECTION",
          "parent": null
      },
      {
          "id": 295,
          "departmentId": 300000043303274,
          "title": "ZAYED PORT CUSTOMS CENTER",
          "titleAr": "ZAYED PORT CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 296,
          "departmentId": 300000038999980,
          "title": "ADMINISTRATIVE AFFAIRS SECTION -AL GUWAIFAT",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION -AL GUWAIFAT",
          "parent": null
      },
      {
          "id": 297,
          "departmentId": 300000043303186,
          "title": "ADMINISTRATIVE AFFAIRS SECTION -KHALIFA PORT",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION -KHALIFA PORT",
          "parent": null
      },
      {
          "id": 298,
          "departmentId": 300000043303255,
          "title": "ARRIVALS SECTION",
          "titleAr": "ARRIVALS SECTION",
          "parent": null
      },
      {
          "id": 299,
          "departmentId": 300000038999555,
          "title": "TRAINING & DEVELOPMENT SECTION - DOF",
          "titleAr": "TRAINING & DEVELOPMENT SECTION - DOF",
          "parent": null
      },
      {
          "id": 300,
          "departmentId": 300000038999539,
          "title": "POLICIES & PROCEDURES SECTION",
          "titleAr": "POLICIES & PROCEDURES SECTION",
          "parent": null
      },
      {
          "id": 301,
          "departmentId": 300000038999949,
          "title": "PASSENGERS INSPECTION SECTION",
          "titleAr": "PASSENGERS INSPECTION SECTION",
          "parent": null
      },
      {
          "id": 302,
          "departmentId": 300000038999862,
          "title": "PROJECTS FOLLOW UP SECTION - CUSTOMS",
          "titleAr": "PROJECTS FOLLOW UP SECTION - CUSTOMS",
          "parent": null
      },
      {
          "id": 303,
          "departmentId": 300000043303410,
          "title": "UNDERSECRETARY OFFICE",
          "titleAr": "UNDERSECRETARY OFFICE",
          "parent": null
      },
      {
          "id": 304,
          "departmentId": 300000038999669,
          "title": "ADMINISTRATIVE AFFAIRS SECTION - AL AIN POST",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION - AL AIN POST",
          "parent": null
      },
      {
          "id": 305,
          "departmentId": 300000038999813,
          "title": "GOODS SECTION",
          "titleAr": "GOODS SECTION",
          "parent": null
      },
      {
          "id": 306,
          "departmentId": 300000046216817,
          "title": "Hazardous Substances, Prevention and Registration Section",
          "titleAr": "Hazardous Substances, Prevention and Registration Section",
          "parent": null
      },
      {
          "id": 307,
          "departmentId": 300000038999559,
          "title": "Operations - Cargo & Post",
          "titleAr": "قسم العمليات - الشحن والبريد",
          "parent": null
      },
      {
          "id": 308,
          "departmentId": 300000001664327,
          "title": "DEVELOPMENT AND STRATEGIC PLANNING SECTION",
          "titleAr": "قسم التطوير والتخطيط الاستراتيجي",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 309,
          "departmentId": 300000001664457,
          "title": "Customs Intelligence Section",
          "titleAr": "قسم التحريات الجمركية",
          "parent": {
              "id": 251,
              "title": "Risk Managemen Division",
              "titleAr": "إدارة إدارة المخاطر"
          }
      },
      {
          "id": 310,
          "departmentId": 300000001664347,
          "title": "Risk Criteria Section",
          "titleAr": "قسم معايير الخطورة",
          "parent": {
              "id": 251,
              "title": "Risk Managemen Division",
              "titleAr": "إدارة إدارة المخاطر"
          }
      },
      {
          "id": 311,
          "departmentId": 300000043303361,
          "title": "HR SYSTEMS DEVELOPMENT SECTION",
          "titleAr": "HR SYSTEMS DEVELOPMENT SECTION",
          "parent": null
      },
      {
          "id": 312,
          "departmentId": 300000038999896,
          "title": "RELEASING SECTION - AD AIRPORT",
          "titleAr": "RELEASING SECTION - AD AIRPORT",
          "parent": null
      },
      {
          "id": 313,
          "departmentId": 300000043303078,
          "title": "CUSTOMS PROCEDURES SECTION - INDUSTRIAL POST",
          "titleAr": "CUSTOMS PROCEDURES SECTION - INDUSTRIAL POST",
          "parent": null
      },
      {
          "id": 314,
          "departmentId": 300000043303289,
          "title": "CUSTOM PROCEDURES SECTION ( CENTRAL POST)",
          "titleAr": "CUSTOM PROCEDURES SECTION ( CENTRAL POST)",
          "parent": null
      },
      {
          "id": 315,
          "departmentId": 300000043303154,
          "title": "CUSTOMERS RELATIONS AND SERVICES SECTION",
          "titleAr": "CUSTOMERS RELATIONS AND SERVICES SECTION",
          "parent": null
      },
      {
          "id": 316,
          "departmentId": 300000043303332,
          "title": "TOURIST VEHICLES SECTION -MAIZED",
          "titleAr": "TOURIST VEHICLES SECTION -MAIZED",
          "parent": null
      },
      {
          "id": 317,
          "departmentId": 300000038999615,
          "title": "COMMUNICATIONS & MARKETING SECTION",
          "titleAr": "COMMUNICATIONS & MARKETING SECTION",
          "parent": null
      },
      {
          "id": 318,
          "departmentId": 300000038999890,
          "title": "AUDIT & COMMERCIAL COMPLIANCE SECTION",
          "titleAr": "AUDIT & COMMERCIAL COMPLIANCE SECTION",
          "parent": null
      },
      {
          "id": 319,
          "departmentId": 300000043303201,
          "title": "CUSTOMER RELATIONS & SERVICES SECTION",
          "titleAr": "CUSTOMER RELATIONS & SERVICES SECTION",
          "parent": null
      },
      {
          "id": 320,
          "departmentId": 300000043303201,
          "title": "CUSTOMER RELATIONS & SERVICES SECTION",
          "titleAr": "CUSTOMER RELATIONS & SERVICES SECTION",
          "parent": null
      },
      {
          "id": 321,
          "departmentId": 300000038999723,
          "title": "ARRIVAL SECTION - AL AIN AIRPORT",
          "titleAr": "ARRIVAL SECTION - AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 322,
          "departmentId": 300000043303405,
          "title": "CARGOS SECTION - AL AIN AIRPORT",
          "titleAr": "CARGOS SECTION - AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 323,
          "departmentId": 300000038999727,
          "title": "CUSTOMS CLEARANCE SECTION -ZAYED PORT",
          "titleAr": "CUSTOMS CLEARANCE SECTION -ZAYED PORT",
          "parent": null
      },
      {
          "id": 324,
          "departmentId": 300000043303300,
          "title": "CUSTOM CENTER - AL AIN POST",
          "titleAr": "CUSTOM CENTER - AL AIN POST",
          "parent": null
      },
      {
          "id": 325,
          "departmentId": 300000038999713,
          "title": "LOCAL PROCUREMENTS SECTION",
          "titleAr": "LOCAL PROCUREMENTS SECTION",
          "parent": null
      },
      {
          "id": 326,
          "departmentId": 300000043303143,
          "title": "COMPENSATION UNIT",
          "titleAr": "COMPENSATION UNIT",
          "parent": null
      },
      {
          "id": 327,
          "departmentId": 300000038999778,
          "title": "CUSTOMS CLEARANCE SECTION -KHATEM ASHAKLEH",
          "titleAr": "CUSTOMS CLEARANCE SECTION -KHATEM ASHAKLEH",
          "parent": null
      },
      {
          "id": 328,
          "departmentId": 300000043303051,
          "title": "HUMAN RESOURCES SECTION",
          "titleAr": "HUMAN RESOURCES SECTION",
          "parent": null
      },
      {
          "id": 329,
          "departmentId": 300000043303392,
          "title": "CUSTOM CENTER - ZAYED PORT",
          "titleAr": "CUSTOM CENTER - ZAYED PORT",
          "parent": null
      },
      {
          "id": 330,
          "departmentId": 300000043303086,
          "title": "STRATEGIC PLANNING & PERFORMANCE MANAGEMENT SECTION",
          "titleAr": "STRATEGIC PLANNING & PERFORMANCE MANAGEMENT SECTION",
          "parent": null
      },
      {
          "id": 331,
          "departmentId": 300000043303304,
          "title": "HAZARDOUS MATERIALS - KHATEM ASHAKLEH",
          "titleAr": "HAZARDOUS MATERIALS - KHATEM ASHAKLEH",
          "parent": null
      },
      {
          "id": 332,
          "departmentId": 300000043303376,
          "title": "Communications & Corporate Marketing",
          "titleAr": "Communications & Corporate Marketing",
          "parent": null
      },
      {
          "id": 333,
          "departmentId": 300000043303200,
          "title": "ADMINISTRATIVE AFFAIRS - MUSSAFAH PORT",
          "titleAr": "ADMINISTRATIVE AFFAIRS - MUSSAFAH PORT",
          "parent": null
      },
      {
          "id": 334,
          "departmentId": 300000001664507,
          "title": "Accounts and Revenue Section",
          "titleAr": "قسم الحسابات والإيرادات",
          "parent": {
              "id": 359,
              "title": "Financial Affairs Division",
              "titleAr": "إدارة الشؤون المالية"
          }
      },
      {
          "id": 335,
          "departmentId": 300000001664567,
          "title": "Operations - Cargo & Post section",
          "titleAr": "قسم العمليات - الشحن والبريد",
          "parent": {
              "id": 282,
              "title": "ABU DHABI AIRPORT CUSTOMS CENTER",
              "titleAr": "مركز جمرك مطار أبوظبي"
          }
      },
      {
          "id": 336,
          "departmentId": 300000001664577,
          "title": "Customs Audit Section",
          "titleAr": "قسم التدقيق الجمركي",
          "parent": {
              "id": 277,
              "title": "Customs Affairs Division",
              "titleAr": "إدارة الشؤون الجمركية"
          }
      },
      {
          "id": 337,
          "departmentId": 300000001664417,
          "title": "Custom Risk Management Division",
          "titleAr": "إدارة إدارة المخاطر الجمركية",
          "parent": {
              "id": 251,
              "title": "Risk Managemen Division",
              "titleAr": "إدارة إدارة المخاطر"
          }
      },
      {
          "id": 338,
          "departmentId": 300000001664587,
          "title": "Office of the Director General",
          "titleAr": "المكتب التنفيذي المدير العام",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 339,
          "departmentId": 300000001664339,
          "title": "Purchasing and Contracts Section",
          "titleAr": "قسم المشتريات والعقود",
          "parent": {
              "id": 396,
              "title": "Services  Division",
              "titleAr": "إدارة الخدمات"
          }
      },
      {
          "id": 340,
          "departmentId": 300000001663810,
          "title": "out",
          "titleAr": "out",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 341,
          "departmentId": 300000038999940,
          "title": "TOURIST VEHICLES SECTION -KHATEM ASHAKLEH",
          "titleAr": "TOURIST VEHICLES SECTION -KHATEM ASHAKLEH",
          "parent": null
      },
      {
          "id": 342,
          "departmentId": 300000043303303,
          "title": "CUSTOMS OPERATIONS CENTER - ABU DHABI",
          "titleAr": "CUSTOMS OPERATIONS CENTER - ABU DHABI",
          "parent": null
      },
      {
          "id": 343,
          "departmentId": 300000043303091,
          "title": "ADMINISTRATION AFFAIRS SECTION - AL GHUWAIFAT",
          "titleAr": "ADMINISTRATION AFFAIRS SECTION - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 344,
          "departmentId": 300000043303364,
          "title": "GOODS INSPECTION SECTION - AD AIRPORT",
          "titleAr": "GOODS INSPECTION SECTION - AD AIRPORT",
          "parent": null
      },
      {
          "id": 345,
          "departmentId": 300000038999577,
          "title": "LEGAL AFFAIRS SECTION - CUSTOMS SECTOR",
          "titleAr": "LEGAL AFFAIRS SECTION - CUSTOMS SECTOR",
          "parent": null
      },
      {
          "id": 346,
          "departmentId": 300000043303233,
          "title": "ADMINISTRATIVE AFFAIRS SECTOR",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTOR",
          "parent": null
      },
      {
          "id": 347,
          "departmentId": 300000043303158,
          "title": "CARGO SECTION -KHATEM ASHAKLEH",
          "titleAr": "CARGO SECTION -KHATEM ASHAKLEH",
          "parent": null
      },
      {
          "id": 348,
          "departmentId": 300000038999802,
          "title": "HAZARDOUS MATERIALS - MAIZED",
          "titleAr": "HAZARDOUS MATERIALS - MAIZED",
          "parent": null
      },
      {
          "id": 349,
          "departmentId": 300000043303316,
          "title": "SUPPORT SERVICES DIVISION",
          "titleAr": "SUPPORT SERVICES DIVISION",
          "parent": null
      },
      {
          "id": 350,
          "departmentId": 300000038999858,
          "title": "STRATEGIC PLANNING AND PERFORMANCE MANAGEMENT OFFICE",
          "titleAr": "STRATEGIC PLANNING AND PERFORMANCE MANAGEMENT OFFICE",
          "parent": null
      },
      {
          "id": 351,
          "departmentId": 300000038999631,
          "title": "CUSTOMS CLEARANCE SECTION - MAIZED",
          "titleAr": "CUSTOMS CLEARANCE SECTION - MAIZED",
          "parent": null
      },
      {
          "id": 352,
          "departmentId": 300000038999818,
          "title": "LOCAL PURCHASING SECTION",
          "titleAr": "LOCAL PURCHASING SECTION",
          "parent": null
      },
      {
          "id": 353,
          "departmentId": 300000038999822,
          "title": "BONDED WAREHOUSES OPERATIONS",
          "titleAr": "BONDED WAREHOUSES OPERATIONS",
          "parent": null
      },
      {
          "id": 354,
          "departmentId": 300000038999879,
          "title": "PROCUREMENT CONTROL DIVISION",
          "titleAr": "PROCUREMENT CONTROL DIVISION",
          "parent": null
      },
      {
          "id": 355,
          "departmentId": 300000038999537,
          "title": "CUSTOM CENTER - AL AIN AIRPORT",
          "titleAr": "CUSTOM CENTER - AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 356,
          "departmentId": 300000001664562,
          "title": "Corporate Performance & Excellence Section",
          "titleAr": "قسم التميز والأداء المؤسسي",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 357,
          "departmentId": 300000001664562,
          "title": "Corporate Performance & Excellence Section",
          "titleAr": "قسم التميز والأداء المؤسسي",
          "parent": {
              "id": 250,
              "title": "Development & Strategic Planning Division",
              "titleAr": "إدارة التطوير و التخطيط الاستراتيجي"
          }
      },
      {
          "id": 358,
          "departmentId": 300000001664572,
          "title": "International Agreements & Intellectual Property Section",
          "titleAr": "قسم الاتفاقيات الدولية والملكية الفكرية",
          "parent": {
              "id": 400,
              "title": "Policies & Governance Division",
              "titleAr": "إدارة السياسات والحوكمة"
          }
      },
      {
          "id": 359,
          "departmentId": 300000001664427,
          "title": "Financial Affairs Division",
          "titleAr": "إدارة الشؤون المالية",
          "parent": {
              "id": 278,
              "title": "Support Services Sector",
              "titleAr": "قطاع الخدمات المساندة"
          }
      },
      {
          "id": 360,
          "departmentId": 300000001664532,
          "title": "Consultancy & Lega Studies Section",
          "titleAr": "قسم الاستشارات والدراسات القانونية",
          "parent": {
              "id": 287,
              "title": "Legal Affairs Division",
              "titleAr": "إدارة الشؤون القانونية"
          }
      },
      {
          "id": 361,
          "departmentId": 300000001664409,
          "title": "Human Resources Division",
          "titleAr": "إدارة الموارد البشرية",
          "parent": {
              "id": 278,
              "title": "Support Services Sector",
              "titleAr": "قطاع الخدمات المساندة"
          }
      },
      {
          "id": 362,
          "departmentId": 300000043303012,
          "title": "TRAINING AND DEVELOPMENT SECTION",
          "titleAr": "TRAINING AND DEVELOPMENT SECTION",
          "parent": null
      },
      {
          "id": 363,
          "departmentId": 300000038999990,
          "title": "LICENSING SECTION",
          "titleAr": "LICENSING SECTION",
          "parent": null
      },
      {
          "id": 364,
          "departmentId": 300000038999880,
          "title": "OUT OF ORGANIZATION STRUCTURE - DOF",
          "titleAr": "OUT OF ORGANIZATION STRUCTURE - DOF",
          "parent": null
      },
      {
          "id": 365,
          "departmentId": 300000043303288,
          "title": "LEGAL AFFAIRS SECTION - CRB",
          "titleAr": "LEGAL AFFAIRS SECTION - CRB",
          "parent": null
      },
      {
          "id": 366,
          "departmentId": 300000038999609,
          "title": "TRUCKS SECTION",
          "titleAr": "TRUCKS SECTION",
          "parent": null
      },
      {
          "id": 367,
          "departmentId": 300000043303058,
          "title": "TOURIST VEHICLES SECTION -AL GUWAIFAT",
          "titleAr": "TOURIST VEHICLES SECTION -AL GUWAIFAT",
          "parent": null
      },
      {
          "id": 368,
          "departmentId": 300000043303185,
          "title": "CARGO SECTION -MAIZED",
          "titleAr": "CARGO SECTION -MAIZED",
          "parent": null
      },
      {
          "id": 369,
          "departmentId": 300000038999762,
          "title": "CUSTOM CENTER - AD AIRPORT",
          "titleAr": "CUSTOM CENTER - AD AIRPORT",
          "parent": null
      },
      {
          "id": 370,
          "departmentId": 300000043303026,
          "title": "ADMINISTRATIVE AFFAIRS SECTION -AL AIN AIRPORT",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION -AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 371,
          "departmentId": 300000038999782,
          "title": "FINANCIAL AFFAIRS SECTION - AL GHUWAIFAT",
          "titleAr": "FINANCIAL AFFAIRS SECTION - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 372,
          "departmentId": 300000043303033,
          "title": "CUSTOMS RISK MANAGEMENT DIVISION",
          "titleAr": "CUSTOMS RISK MANAGEMENT DIVISION",
          "parent": null
      },
      {
          "id": 373,
          "departmentId": 300000043303038,
          "title": "IT SECURITY SECTION",
          "titleAr": "IT SECURITY SECTION",
          "parent": null
      },
      {
          "id": 374,
          "departmentId": 300000038999912,
          "title": "HAZARDOUS MATERIALS - ABU DHABI AIRPORT",
          "titleAr": "HAZARDOUS MATERIALS - ABU DHABI AIRPORT",
          "parent": null
      },
      {
          "id": 375,
          "departmentId": 300000038999689,
          "title": "OUT OF ORGANIZATION STRUCTURE - CUSTOM",
          "titleAr": "OUT OF ORGANIZATION STRUCTURE - CUSTOM",
          "parent": null
      },
      {
          "id": 376,
          "departmentId": 300000038999833,
          "title": "ADMINISTRATION AFFAIRS SECTION - KHATEM ASHAKILA",
          "titleAr": "ADMINISTRATION AFFAIRS SECTION - KHATEM ASHAKILA",
          "parent": null
      },
      {
          "id": 377,
          "departmentId": 300000038999593,
          "title": "CUSTOMS INVESTIGATIONS SECTION",
          "titleAr": "CUSTOMS INVESTIGATIONS SECTION",
          "parent": null
      },
      {
          "id": 378,
          "departmentId": 300000038999874,
          "title": "Services Division",
          "titleAr": "إدارة الخدمات",
          "parent": null
      },
      {
          "id": 379,
          "departmentId": 300000038999971,
          "title": "CUSTOM CENTER - AL GHUWAIFAT",
          "titleAr": "CUSTOM CENTER - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 380,
          "departmentId": 300000038999910,
          "title": "AIRPORTS & POSTS CUSTOMS OPERATIONS DIVISION",
          "titleAr": "AIRPORTS & POSTS CUSTOMS OPERATIONS DIVISION",
          "parent": null
      },
      {
          "id": 381,
          "departmentId": 300000043303072,
          "title": "FOLLOW UP SECTION",
          "titleAr": "FOLLOW UP SECTION",
          "parent": null
      },
      {
          "id": 382,
          "departmentId": 300000043303347,
          "title": "GENERAL SERVICES SECTION - CUSTOMS",
          "titleAr": "GENERAL SERVICES SECTION - CUSTOMS",
          "parent": null
      },
      {
          "id": 383,
          "departmentId": 300000038999814,
          "title": "ZAYED PORT OPERATIONS SECTION",
          "titleAr": "ZAYED PORT OPERATIONS SECTION",
          "parent": null
      },
      {
          "id": 384,
          "departmentId": 300000038999538,
          "title": "COMMUNICATION SECTION",
          "titleAr": "COMMUNICATION SECTION",
          "parent": null
      },
      {
          "id": 385,
          "departmentId": 300000043303093,
          "title": "CUSTOMS OPERATIONS CENTER - AL AIN",
          "titleAr": "CUSTOMS OPERATIONS CENTER - AL AIN",
          "parent": null
      },
      {
          "id": 386,
          "departmentId": 300000038999838,
          "title": "RELEASING SECTION - KHATEM ASHAKILAH",
          "titleAr": "RELEASING SECTION - KHATEM ASHAKILAH",
          "parent": null
      },
      {
          "id": 387,
          "departmentId": 300000038999798,
          "title": "ORGANIZATION EXCELLENCE SECTION",
          "titleAr": "ORGANIZATION EXCELLENCE SECTION",
          "parent": null
      },
      {
          "id": 388,
          "departmentId": 300000038999964,
          "title": "VEHICLES' FEES SECTION - AL AIN POST",
          "titleAr": "VEHICLES' FEES SECTION - AL AIN POST",
          "parent": null
      },
      {
          "id": 389,
          "departmentId": 300000038999752,
          "title": "CUSTOMS SYSTEMS AND PROCEDURES DEVELOPMENT SECTION",
          "titleAr": "CUSTOMS SYSTEMS AND PROCEDURES DEVELOPMENT SECTION",
          "parent": null
      },
      {
          "id": 390,
          "departmentId": 300000001664437,
          "title": "Payments Section",
          "titleAr": "قسم المدفوعات",
          "parent": {
              "id": 359,
              "title": "Financial Affairs Division",
              "titleAr": "إدارة الشؤون المالية"
          }
      },
      {
          "id": 391,
          "departmentId": 300000001664387,
          "title": "Operations – Passengers section",
          "titleAr": "قسم العمليات - المسافرين",
          "parent": {
              "id": 282,
              "title": "ABU DHABI AIRPORT CUSTOMS CENTER",
              "titleAr": "مركز جمرك مطار أبوظبي"
          }
      },
      {
          "id": 392,
          "departmentId": 300000001664319,
          "title": "Application Development Section",
          "titleAr": "قسم تطوير التطبيقات",
          "parent": {
              "id": 249,
              "title": "Information Technology Division",
              "titleAr": "إدارة تقنية المعلومات"
          }
      },
      {
          "id": 393,
          "departmentId": 300000001664449,
          "title": "Al Ain Airport Customs Center",
          "titleAr": "مركز جمرك مطار العين",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 394,
          "departmentId": 300000001664612,
          "title": "Information Security Unit",
          "titleAr": "وحدة أمن المعلومات",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 395,
          "departmentId": 300000001664622,
          "title": "Customs Policies & Procedures Section",
          "titleAr": "قسم السياسات والإجراءات الجمركية",
          "parent": {
              "id": 400,
              "title": "Policies & Governance Division",
              "titleAr": "إدارة السياسات والحوكمة"
          }
      },
      {
          "id": 396,
          "departmentId": 300000001664349,
          "title": "Services  Division",
          "titleAr": "إدارة الخدمات",
          "parent": {
              "id": 278,
              "title": "Support Services Sector",
              "titleAr": "قطاع الخدمات المساندة"
          }
      },
      {
          "id": 397,
          "departmentId": 300000001664429,
          "title": "Mazied Customs Center",
          "titleAr": "مركز جمرك مزيد",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 398,
          "departmentId": 300000001664462,
          "title": "Customs Security Section",
          "titleAr": "قسم الأمن الجمركي",
          "parent": {
              "id": 251,
              "title": "Risk Managemen Division",
              "titleAr": "إدارة إدارة المخاطر"
          }
      },
      {
          "id": 399,
          "departmentId": 300000001664527,
          "title": "Corporate Communications & Customer Happiness Division",
          "titleAr": "إدارة الاتصال المؤسسي وسعادة المتعاملين",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 400,
          "departmentId": 300000001664537,
          "title": "Policies & Governance Division",
          "titleAr": "إدارة السياسات والحوكمة",
          "parent": {
              "id": 461,
              "title": "Technical Affairs Sector",
              "titleAr": "قطاع الشؤون الفنية"
          }
      },
      {
          "id": 401,
          "departmentId": 300000043303270,
          "title": "CONTROL OFFICE",
          "titleAr": "CONTROL OFFICE",
          "parent": null
      },
      {
          "id": 402,
          "departmentId": 300000043303246,
          "title": "CUSTOMS CLEARANCE SECTION -AL GUWAIFAT",
          "titleAr": "CUSTOMS CLEARANCE SECTION -AL GUWAIFAT",
          "parent": null
      },
      {
          "id": 403,
          "departmentId": 300000043303018,
          "title": "GOVERMENT RELATIONS",
          "titleAr": "GOVERMENT RELATIONS",
          "parent": null
      },
      {
          "id": 404,
          "departmentId": 300000043303098,
          "title": "APPLICATIONS SECTION",
          "titleAr": "APPLICATIONS SECTION",
          "parent": null
      },
      {
          "id": 405,
          "departmentId": 300000038999649,
          "title": "RISK MANAGEMENT SECTION",
          "titleAr": "RISK MANAGEMENT SECTION",
          "parent": null
      },
      {
          "id": 406,
          "departmentId": 300000038999613,
          "title": "KHALIFA PORT OPERATIONS SECTION",
          "titleAr": "KHALIFA PORT OPERATIONS SECTION",
          "parent": null
      },
      {
          "id": 407,
          "departmentId": 300000043303334,
          "title": "DIRECTOR GENERAL - CUSTOMS",
          "titleAr": "DIRECTOR GENERAL - CUSTOMS",
          "parent": null
      },
      {
          "id": 408,
          "departmentId": 300000038999738,
          "title": "HOUSING",
          "titleAr": "HOUSING",
          "parent": null
      },
      {
          "id": 409,
          "departmentId": 300000043303214,
          "title": "CUSTOMS LICENSES SECTION",
          "titleAr": "CUSTOMS LICENSES SECTION",
          "parent": null
      },
      {
          "id": 410,
          "departmentId": 300000043303188,
          "title": "Operations - Passengers Section",
          "titleAr": "قسم العمليات - المسافرين",
          "parent": null
      },
      {
          "id": 411,
          "departmentId": 300000043303331,
          "title": "ADMINISTRATIVE AFFAIRS SECTION - ABU DHABI AIRPORT",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION - ABU DHABI AIRPORT",
          "parent": null
      },
      {
          "id": 412,
          "departmentId": 300000038999970,
          "title": "HAZARDOUS MATERIALS - ZAYED PORT",
          "titleAr": "HAZARDOUS MATERIALS - ZAYED PORT",
          "parent": null
      },
      {
          "id": 413,
          "departmentId": 300000043303218,
          "title": "GOODS SECTION - AD AIRPORT",
          "titleAr": "GOODS SECTION - AD AIRPORT",
          "parent": null
      },
      {
          "id": 414,
          "departmentId": 300000043303317,
          "title": "PROCUREMENTS AND CONTRACTS SECTION",
          "titleAr": "PROCUREMENTS AND CONTRACTS SECTION",
          "parent": null
      },
      {
          "id": 415,
          "departmentId": 300000038999591,
          "title": "TRAINING AND DEVELOPMENT SECTION - Customs",
          "titleAr": "TRAINING AND DEVELOPMENT SECTION - Customs",
          "parent": null
      },
      {
          "id": 416,
          "departmentId": 300000043303173,
          "title": "GENERAL DIRECTORATE OF CUSTOMS",
          "titleAr": "GENERAL DIRECTORATE OF CUSTOMS",
          "parent": null
      },
      {
          "id": 417,
          "departmentId": 300000043303394,
          "title": "TRUCKS SECTION - AL GHUWAIFAT",
          "titleAr": "TRUCKS SECTION - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 418,
          "departmentId": 300000038999595,
          "title": "GENERAL SERVICES SECTION",
          "titleAr": "GENERAL SERVICES SECTION",
          "parent": null
      },
      {
          "id": 419,
          "departmentId": 300000043303169,
          "title": "CUSTOMS CLEARANCE SECTION - ABU DHABI AIRPORT",
          "titleAr": "CUSTOMS CLEARANCE SECTION - ABU DHABI AIRPORT",
          "parent": null
      },
      {
          "id": 420,
          "departmentId": 300000038999931,
          "title": "ACCOUNTS & COLLECTIONS SECTION",
          "titleAr": "ACCOUNTS & COLLECTIONS SECTION",
          "parent": null
      },
      {
          "id": 421,
          "departmentId": 300000038999536,
          "title": "MUSSAFAH PORT CUSTOMS CENTER",
          "titleAr": "MUSSAFAH PORT CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 422,
          "departmentId": 300000043303128,
          "title": "ADMINISTRATIVE AFFAIRS SECTION -KHATEM ASHAKLEH",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION -KHATEM ASHAKLEH",
          "parent": null
      },
      {
          "id": 423,
          "departmentId": 300000043303377,
          "title": "CUSTOMS POLICIES AND PROCEDURES SECTION",
          "titleAr": "CUSTOMS POLICIES AND PROCEDURES SECTION",
          "parent": null
      },
      {
          "id": 424,
          "departmentId": 300000038999705,
          "title": "FINANCIAL AFFAIRS SECTION -ZAYED PORT",
          "titleAr": "FINANCIAL AFFAIRS SECTION -ZAYED PORT",
          "parent": null
      },
      {
          "id": 425,
          "departmentId": 300000043303346,
          "title": "CARGO SECTION -AL AIN AIRPORT",
          "titleAr": "CARGO SECTION -AL AIN AIRPORT",
          "parent": null
      },
      {
          "id": 426,
          "departmentId": 300000043303113,
          "title": "PASSENGER INSPECTION SECTION - AD AIRPORT",
          "titleAr": "PASSENGER INSPECTION SECTION - AD AIRPORT",
          "parent": null
      },
      {
          "id": 427,
          "departmentId": 300000043303031,
          "title": "HAZARDOUS MATERIALS SECTION",
          "titleAr": "HAZARDOUS MATERIALS SECTION",
          "parent": null
      },
      {
          "id": 428,
          "departmentId": 300000043303005,
          "title": "FINANCIAL AFFAIRS SECTION -AL GUWAIFAT",
          "titleAr": "FINANCIAL AFFAIRS SECTION -AL GUWAIFAT",
          "parent": null
      },
      {
          "id": 429,
          "departmentId": 300000046216778,
          "title": "AL HILI CUSTOMS CENTER",
          "titleAr": "AL HILI CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 430,
          "departmentId": 300000001664557,
          "title": "Networks Section",
          "titleAr": "قسم الشبكات",
          "parent": {
              "id": 249,
              "title": "Information Technology Division",
              "titleAr": "إدارة تقنية المعلومات"
          }
      },
      {
          "id": 431,
          "departmentId": 300000001664512,
          "title": "Environment, Health & Safety Section",
          "titleAr": "قسم البيئة والصحة والسلامة",
          "parent": {
              "id": 338,
              "title": "Office of the Director General",
              "titleAr": "المكتب التنفيذي المدير العام"
          }
      },
      {
          "id": 432,
          "departmentId": 300000001664357,
          "title": "STATISTICS SECTION",
          "titleAr": "قسم الاحصاء",
          "parent": {
              "id": 274,
              "title": "Project Management Division",
              "titleAr": "إدارة المشاريع"
          }
      },
      {
          "id": 433,
          "departmentId": 300000001664522,
          "title": "Recruitment Section",
          "titleAr": "قسم التوظيف",
          "parent": {
              "id": 361,
              "title": "Human Resources Division",
              "titleAr": "إدارة الموارد البشرية"
          }
      },
      {
          "id": 434,
          "departmentId": 300000001664602,
          "title": "Budget Section",
          "titleAr": "قسم الموازنة",
          "parent": {
              "id": 359,
              "title": "Financial Affairs Division",
              "titleAr": "إدارة الشؤون المالية"
          }
      },
      {
          "id": 435,
          "departmentId": 300000043303053,
          "title": "HAZARDOUS MATERIALS - AL GUWAIFAT",
          "titleAr": "HAZARDOUS MATERIALS - AL GUWAIFAT",
          "parent": null
      },
      {
          "id": 436,
          "departmentId": 300000038999951,
          "title": "ADMINISTRATION AFFAIRS SECTION - AD AIRPORT",
          "titleAr": "ADMINISTRATION AFFAIRS SECTION - AD AIRPORT",
          "parent": null
      },
      {
          "id": 437,
          "departmentId": 300000043303362,
          "title": "PAYMENT SECTION",
          "titleAr": "PAYMENT SECTION",
          "parent": null
      },
      {
          "id": 438,
          "departmentId": 300000038999553,
          "title": "Khatem Ashakleh Customs Center",
          "titleAr": "Khatem Ashakleh Customs Center",
          "parent": null
      },
      {
          "id": 439,
          "departmentId": 300000038999932,
          "title": "Al Guwaifat Customs Center",
          "titleAr": "Al Guwaifat Customs Center",
          "parent": null
      },
      {
          "id": 440,
          "departmentId": 300000043303110,
          "title": "GOODS INSPECTION SECTION",
          "titleAr": "GOODS INSPECTION SECTION",
          "parent": null
      },
      {
          "id": 441,
          "departmentId": 300000038999611,
          "title": "ABU DHABI POST OPERATIONS",
          "titleAr": "ABU DHABI POST OPERATIONS",
          "parent": null
      },
      {
          "id": 442,
          "departmentId": 300000038999651,
          "title": "TOURIST VEHICLES SECTION - MAZIED",
          "titleAr": "TOURIST VEHICLES SECTION - MAZIED",
          "parent": null
      },
      {
          "id": 443,
          "departmentId": 300000043303046,
          "title": "ADMINISTRATIVE AFFAIRS SECTION -MAIZED",
          "titleAr": "ADMINISTRATIVE AFFAIRS SECTION -MAIZED",
          "parent": null
      },
      {
          "id": 444,
          "departmentId": 300000043303391,
          "title": "SERVICE DEVELOPMENT AND FOLLOW UP SECTION",
          "titleAr": "SERVICE DEVELOPMENT AND FOLLOW UP SECTION",
          "parent": null
      },
      {
          "id": 445,
          "departmentId": 300000038999854,
          "title": "OUT OF ORGANIZATION STRUCTURE - CRB",
          "titleAr": "OUT OF ORGANIZATION STRUCTURE - CRB",
          "parent": null
      },
      {
          "id": 446,
          "departmentId": 300000038999772,
          "title": "GOVERNMENT RELATIONS UNIT",
          "titleAr": "GOVERNMENT RELATIONS UNIT",
          "parent": null
      },
      {
          "id": 447,
          "departmentId": 300000038999921,
          "title": "TRAINING & DEVELOPMENT SECTION - CUST",
          "titleAr": "TRAINING & DEVELOPMENT SECTION - CUST",
          "parent": null
      },
      {
          "id": 448,
          "departmentId": 300000038999853,
          "title": "LEGAL AFFAIRS SECTION - CB",
          "titleAr": "LEGAL AFFAIRS SECTION - CB",
          "parent": null
      },
      {
          "id": 449,
          "departmentId": 300000038999693,
          "title": "SYSTEMS SECTION",
          "titleAr": "SYSTEMS SECTION",
          "parent": null
      },
      {
          "id": 450,
          "departmentId": 300000043303126,
          "title": "MUDEEF CUSTOMS CENTER",
          "titleAr": "MUDEEF CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 451,
          "departmentId": 300000039000000,
          "title": "JUSTICE & SECURITY SECTION",
          "titleAr": "JUSTICE & SECURITY SECTION",
          "parent": null
      },
      {
          "id": 452,
          "departmentId": 300000043303230,
          "title": "INTELLIGENCE SECTION",
          "titleAr": "INTELLIGENCE SECTION",
          "parent": null
      },
      {
          "id": 453,
          "departmentId": 300000038999876,
          "title": "General Administration of Customs",
          "titleAr": "General Administration of Customs",
          "parent": null
      },
      {
        "id":1999,
        "departmentId":1234321,
        "titleAr":"المكتب التنفيذي لقطاع  مكتب المدير العام",
        "title":"Executive Office of the General Director’s Office Sector",
        "parent":null
      },
      {
          "id": 454,
          "departmentId": 300000001664477,
          "title": "Internal Audit Office",
          "titleAr": "مكتب التدقيق الداخلي",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 455,
          "departmentId": 300000001664399,
          "title": "Customs Licensing Section",
          "titleAr": "قسم التراخيص الجمركية",
          "parent": {
              "id": 277,
              "title": "Customs Affairs Division",
              "titleAr": "إدارة الشؤون الجمركية"
          }
      },
      {
          "id": 456,
          "departmentId": 300000001664467,
          "title": "ACCOUNTS SECTION",
          "titleAr": "قسم الحسابات",
          "parent": {
              "id": 359,
              "title": "Financial Affairs Division",
              "titleAr": "إدارة الشؤون المالية"
          }
      },
      {
          "id": 457,
          "departmentId": 300000001663918,
          "title": "Al Ain Post Customs Center",
          "titleAr": "Al Ain Post Customs Center",
          "parent": {
              "id": 8,
              "title": "Operations Sector",
              "titleAr": "قطاع العمليات"
          }
      },
      {
          "id": 458,
          "departmentId": 300000001663942,
          "title": "Higher Management",
          "titleAr": "الإدارة العليا",
          "parent": null
      },
      {
        "id": 1283,
        "departmentId": 300000001664551,
        "title": "General Director's Office Sector",
        "titleAr": "قطاع مكتب المدير العام",
        "parent": {
            "id": 458,
            "title": "Higher Management",
            "titleAr": "الإدارة العليا"
        }
    },
      {
          "id": 459,
          "departmentId": 300000001664552,
          "title": "Director General",
          "titleAr": "مكتب المدير العام",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 460,
          "departmentId": 300000001664617,
          "title": "Facilitation and Compliance Section - Exemptions",
          "titleAr": "قسم التسهيل والالتزام (الإعفاءات)",
          "parent": {
              "id": 277,
              "title": "Customs Affairs Division",
              "titleAr": "إدارة الشؤون الجمركية"
          }
      },
      {
          "id": 461,
          "departmentId": 300000001664337,
          "title": "Technical Affairs Sector",
          "titleAr": "قطاع الشؤون الفنية",
          "parent": {
              "id": 458,
              "title": "Higher Management",
              "titleAr": "الإدارة العليا"
          }
      },
      {
          "id": 462,
          "departmentId": 300000001664502,
          "title": "Tariff, Value and Origin Section",
          "titleAr": "قسم التعرفة والقيمة والمنشأ",
          "parent": {
              "id": 277,
              "title": "Customs Affairs Division",
              "titleAr": "إدارة الشؤون الجمركية"
          }
      },
      {
          "id": 463,
          "departmentId": 300000038999744,
          "title": "LEGAL AFFAIRS SECTION - CUST",
          "titleAr": "LEGAL AFFAIRS SECTION - CUST",
          "parent": null
      },
      {
          "id": 464,
          "departmentId": 300000043303257,
          "title": "CONTROL & QUALITY SECTION",
          "titleAr": "CONTROL & QUALITY SECTION",
          "parent": null
      },
      {
          "id": 465,
          "departmentId": 300000043303272,
          "title": "FREE ZONES SECTION",
          "titleAr": "FREE ZONES SECTION",
          "parent": null
      },
      {
          "id": 466,
          "departmentId": 300000038999589,
          "title": "OPERATIONS AND SUPPORT SECTION",
          "titleAr": "OPERATIONS AND SUPPORT SECTION",
          "parent": null
      },
      {
          "id": 467,
          "departmentId": 300000038999709,
          "title": "RELEASING SECTION - AL GHUWAIFAT",
          "titleAr": "RELEASING SECTION - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 468,
          "departmentId": 300000038999834,
          "title": "LAND CUSTOMS OPERATIONS DIVISION",
          "titleAr": "LAND CUSTOMS OPERATIONS DIVISION",
          "parent": null
      },
      {
          "id": 469,
          "departmentId": 300000038999684,
          "title": "TRAINING AND CAPABILITY BUILDING DIVISION",
          "titleAr": "TRAINING AND CAPABILITY BUILDING DIVISION",
          "parent": null
      },
      {
          "id": 470,
          "departmentId": 300000038999922,
          "title": "MARITIME CUSTOMS OPERATIONS DIVISION",
          "titleAr": "MARITIME CUSTOMS OPERATIONS DIVISION",
          "parent": null
      },
      {
          "id": 471,
          "departmentId": 300000043303171,
          "title": "EMPLOYEES AFFAIRS SECTION",
          "titleAr": "EMPLOYEES AFFAIRS SECTION",
          "parent": null
      },
      {
          "id": 472,
          "departmentId": 300000043303141,
          "title": "TOURIST VEHICLES SECTION - AL GHUWAIFAT",
          "titleAr": "TOURIST VEHICLES SECTION - AL GHUWAIFAT",
          "parent": null
      },
      {
          "id": 473,
          "departmentId": 300000038999774,
          "title": "INVESTIGATION & PROSECUTION SECTION",
          "titleAr": "INVESTIGATION & PROSECUTION SECTION",
          "parent": null
      },
      {
          "id": 474,
          "departmentId": 300000038999984,
          "title": "CARGOES SECTION - KHATEM ASHAKILAH",
          "titleAr": "CARGOES SECTION - KHATEM ASHAKILAH",
          "parent": null
      },
      {
          "id": 475,
          "departmentId": 300000038999558,
          "title": "COMMUNICATION AND ORGANIZATION MARKETING SECTION",
          "titleAr": "COMMUNICATION AND ORGANIZATION MARKETING SECTION",
          "parent": null
      },
      {
          "id": 476,
          "departmentId": 300000038999842,
          "title": "CUSTOMS SUPPORT & ASSISTANCE DIVISION",
          "titleAr": "CUSTOMS SUPPORT & ASSISTANCE DIVISION",
          "parent": null
      },
      {
          "id": 477,
          "departmentId": 300000038999575,
          "title": "EMPLOYEE AFFAIRS SECTION",
          "titleAr": "EMPLOYEE AFFAIRS SECTION",
          "parent": null
      },
      {
          "id": 478,
          "departmentId": 300000043303013,
          "title": "ABU DHABI POST CUSTOMS CENTER",
          "titleAr": "ABU DHABI POST CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 479,
          "departmentId": 300000038999758,
          "title": "Environmental health and safety section",
          "titleAr": "Environmental health and safety section",
          "parent": null
      },
      {
          "id": 480,
          "departmentId": 300000043303106,
          "title": "OUT OF ORGANIZATION STRUCTURE - CUSTOMS SECTOR",
          "titleAr": "OUT OF ORGANIZATION STRUCTURE - CUSTOMS SECTOR",
          "parent": null
      },
      {
          "id": 481,
          "departmentId": 300000038999991,
          "title": "HAZARDOUS MATERIALS -KHALIFA PORT",
          "titleAr": "HAZARDOUS MATERIALS -KHALIFA PORT",
          "parent": null
      },
      {
          "id": 482,
          "departmentId": 300000038999685,
          "title": "CUSTOM CENTER - MAZIED",
          "titleAr": "CUSTOM CENTER - MAZIED",
          "parent": null
      },
      {
          "id": 483,
          "departmentId": 300000043303379,
          "title": "RECRUITMENT AND MANPOWER PLANNING SECTION",
          "titleAr": "RECRUITMENT AND MANPOWER PLANNING SECTION",
          "parent": null
      },
      {
          "id": 484,
          "departmentId": 300000043303349,
          "title": "Maized Customs Center",
          "titleAr": "Maized Customs Center",
          "parent": null
      },
      {
          "id": 485,
          "departmentId": 300000043303259,
          "title": "BONDED WAREHOUSES CUSTOMS CENTER",
          "titleAr": "BONDED WAREHOUSES CUSTOMS CENTER",
          "parent": null
      },
      {
          "id": 486,
          "departmentId": 300000043303319,
          "title": "CUSTOMS OPERATIONS CENTER - WESTERN REGION",
          "titleAr": "CUSTOMS OPERATIONS CENTER - WESTERN REGION",
          "parent": null
      },
      {
          "id": 487,
          "departmentId": 300000038999665,
          "title": "OPERATION SECTION",
          "titleAr": "OPERATION SECTION",
          "parent": null
      }
  ],
  "total": 248
}
      return res
      
      
      // new Promise(function (resolve, reject) {
      //     $.ajax({
      //         url: URL,
      //         type: "GET",
      //         headers: {
      //             Authorization: "Bearer " + Liferay.authToken,
      //             'ApiKey': 'TUlJQ1dnSUJBQUtCZ0hjRzVMWkdXSXpRUnhwdW54VDE5NmozZTkzL0I5eTZETTB3ZlBaejhyTDFZek5VZFNIUgpVclZ6TFBZUDUydTI0cU9VSUhyU2dZRXVlWEFwcTBQK2VTT1pMdk5xZ3V3SVR0Yk9OQTNLU0srOWxWaFpyQy9BClNmU2dON3psRk54UFJDMGdwaFV4QUJZdEFpdlQzR2xQMWZDK1BheFpGM2prTFFUbm5YTW9jREk3QWdNQkFBRUMKZ1lCcUt4cnN2eGlUR2VDaVloUEI1Wmd2L2ZoZHp2TGJYcFMybmM2SkltbFVXVzlQeE1EcUZrVlpGay8vZDdZcgpyU2pCVWdvYXBCUGgvMnRRc2NwVFR2UUxvYW8vTHE5NTNSdEx6dlViSVFoUjkrWWpIaml2aVM5THZSK3JjSUhyCk5Zdzc1YnlGM3lNMGpvK1dSbXpBVlpJd1AwVWVhcEpZTWN2S1FraElGYWNyMlFKQkFNK1Y5T3pFR2VBN3BTMXUKVFR6UVJkSEQwQjNUMkRSdFl0OHI4RVFTamErNEp4T0VNTGpMV1dycUkxWi9mOThpK3JUN3B1d0lRNUxMVm92ZgpkK0dzWTFVQ1FRQ1N5WGsyVnBaTVFxVFF0NS9MZ1dET2tacHV1WGt6cEQ5bmRReDB6cXIxWGF6NkNvNlVnQS82CmtxckV2YWJCWXBqNmlya2VTQTF0SlhUMmptcWYzVjlQQWtBL1I0MDBKOHRqaVlzZXdFTVhTTDRmNWJzcGZJeXAKM3JhSEpaUEdqSWxZaWFDUDJIb3B1d04xRGc3YnJWNURuUndqMDVyYzFPQVVmWnZTWTdyZHRubEpBa0FQZVhmeQoxNHYrdkNQZDhRM0NpWUFvSnNkdUZ0V0ZNVEtSK0kvNG5IVC9hd0c2Vm5TVGlUQ21ET0k5M1hSLy9LSDkvN1BtClVsaEFBbXZqTmo1ZFhod1hBa0JOYnp4OVhDY1RoTlpkNUNnV1NrY09FRVEwL3FxcUJiSW5NRmZOL2l1eEVaUUEKUjVvRVdOTWtSeTRzeHY0dXVOOFg0YnRQeUw5WHdvcEJIMGtZQTEvNQ==',
      //             languageId: Liferay.ThemeDisplay.getLanguageId(),

      //         },
      //         success: function (res) {
      //             resolve(res); // Resolve the Promise with the successful response data
      //         },
      //         error: function (error) {
      //             reject(error); // Reject the Promise with the error object
      //         },
      //     });
      // });
  }
  async function renderChart() {
      $('.level-2-wrapper').html(`<li><ol class="level-3-wrapper" id="level-3-wrapper"></ol></li>`);

      departments = await getAllDepartments();
      $(".loader-container").removeClass("d-none");
      $(".loader-container").addClass("d-flex");
      var lvl1Array = orgJson.BOD.lvl1.subordinate;

      for (var i = 0; i < lvl1Array.length; i++) {
          var divisionDetails = getDepartments(lvl1Array[i].divisionID);
          lvl1Array[i]["titleEn"] = divisionDetails.title;
          lvl1Array[i]["titleAr"] = divisionDetails.titleAr;

          if (lvl1Array[i].subSections.length > 0) {
              for (
                  var subIndex = 0;
                  subIndex < lvl1Array[i].subSections.length;
                  subIndex++
              ) {
                  var res = getDepartments(
                      lvl1Array[i].subSections[subIndex].sectionID
                  );
                  lvl1Array[i].subSections[subIndex]["titleEn"] = res.title;
                  lvl1Array[i].subSections[subIndex]["titleAr"] = res.titleAr;
              }
          }
      }

      var lvl2Array = orgJson.BOD.lvl2.sectors;

      for (var i = 0; i < lvl2Array.length; i++) {
          var sectorDetails = getDepartments(lvl2Array[i].sectorId);
          lvl2Array[i]["titleEn"] = sectorDetails.title;
          lvl2Array[i]["titleAr"] = sectorDetails.titleAr;
          for (var dIndex = 0; dIndex < lvl2Array[i].departments.length; dIndex++) {
              var divisionDetails = getDepartments(
                  lvl2Array[i].departments[dIndex].divisionID
              );

              if (lvl2Array[i].sectorId == '300000001664419') {
                  lvl2Array[i].departments[dIndex]["titleAr"] =
                      'مراكز قطاع العمليات';
                  lvl2Array[i].departments[dIndex]["titleEn"] =
                      'Operations Sector Centers';;
              } else {
                  lvl2Array[i].departments[dIndex]["titleEn"] =
                      divisionDetails.title;
                  lvl2Array[i].departments[dIndex]["titleAr"] =
                      divisionDetails.titleAr;
              }

              if (lvl2Array[i].departments[dIndex].depSections.length > 0) {
                  for (
                      var subIndex = 0;
                      subIndex < lvl2Array[i].departments[dIndex].depSections.length;
                      subIndex++
                  ) {
                      var res = getDepartments(
                          lvl2Array[i].departments[dIndex].depSections[subIndex].sectionID
                      );
                      lvl2Array[i].departments[dIndex].depSections[subIndex]["titleEn"] =
                          res.title;
                      lvl2Array[i].departments[dIndex].depSections[subIndex]["titleAr"] =
                          res.titleAr;
                  }
              }
          }
      }
      $(".loader-container").addClass("d-none");
      $(".loader-container").removeClass("d-flex");
      $(".chart-container").removeClass("d-none");
      addLvls();
  }

  $(document).on("click", ".toggleParent", function () {
        event.stopPropagation(); // Stop event bubbling if necessary

      $(this).closest("div.level-3").toggleClass("collapsed");
  });

  $(".nextPage").on("click", function () {
        event.stopPropagation(); // Stop event bubbling if necessary

      lastPage = Math.ceil(tempEmpArray?.total / 12);
      if (pageNum < lastPage) {
          pageNum = pageNum + 1;
          getEmployees(selectedSection, sectionId);
      }
  });

  $(".prevPage").on("click", function () {
        event.stopPropagation(); // Stop event bubbling if necessary

      if (pageNum > 1) {
          pageNum--;
          getEmployees(selectedSection, sectionId);
      }
  });

  function addLvls() {
      let subIndex = 0;
      let item = "";

      for (let sub of orgJson.BOD.lvl1.subordinate) {
          let lvl4 = addLvl4(sub?.subSections);
          let title = languageId == "en_US" ? sub.titleEn : sub.titleAr;
        if(subIndex==0){
          item=`<div class="level-2 rectangle">
<div class="sector--desc">
<div class="sector--title font-weight-bold mb-2">
${title}
</div>

<!-- <button class="btn-sm btn more-details" 
 data-title="Technical Affairs Sector" data-id="300000001664337"  >More</button>!-->
</div>
</div>`
        }else{
          item = `<li class="level-3-li">
<div class="level-3 rectangle collapsed">
<div class="sector--title toggleParent"  data-toggle="collapse"
href="${"#collapse-lvl1" + subIndex}" role="button" aria-expanded="false"
aria-controls="${"collapse-lvl1" + subIndex}">
<span>${title} </span>

<span class="collapse--btn" data-toggle="collapse" href="${"#collapse-lvl1" + subIndex
              }" role="button"
aria-expanded="false" aria-controls="${"collapse-lvl1" + subIndex}">
<i class="icon-chevron-up text-white m-auto"></i>
</span>
</div>

<div class="sector--desc">
<!-- <button class="btn-sm btn more-details"
data-title="${title}" data-id="${sub.divisionID}">${morelbl}</button>!-->
</div>
</div>

<ol class="level-4-wrapper collapse" id="${"collapse-lvl1" + subIndex}">
${lvl4}
</ol>
</li>`};
          subIndex++;
          $("#level-3-wrapper").append(item);
      }

      let sectorItem = "";
      let sectorIndex = 0;
      for (let sector of orgJson.BOD.lvl2.sectors) {
          let departments = addLvl2sectors(sector, sectorIndex);
          let title = languageId == "en_US" ? sector.titleEn : sector.titleAr;
          sectorItem = `<li>
<div class="level-2 rectangle">
<div class="sector--desc">
<div class="sector--title font-weight-bold mb-2">
${title}
</div>

<!-- <button class="btn-sm btn more-details" 
 data-title="${title}" data-id="${sector.sectorId}"  >${morelbl}</button>!-->
</div>
</div>
<ol class="level-3-wrapper">
${departments}</ol>

</li>`;
          sectorIndex++;
          $(".level-2-wrapper").append(sectorItem);
      }
  }

  // $(document).on("click", ".more-details", function () {

  //     event.stopPropagation(); // Stop event bubbling if necessary

  //     let title = $(this).attr("data-title");
  //     let id = $(this).attr("data-id");

  //     getEmployees(title, id);
  //     $(".chart-container").addClass("d-none");
  // });

  function addLvl4(sub) {
      let res = "";
      for (let section of sub) {
          let title = languageId == "en_US" ? section.titleEn : section.titleAr;
          res += `<li>
<p class="level-4 rectangle more-details" data-title="${title}" data-id="${section.sectionID}">
${title}
</p>
</li>`;
      }
      return res;
  }

  function addLvl2sectors(sector, sectorIndex) {
      let depIndex = 0;
      let departmentItem = "";

      for (let department of sector?.departments) {
          let lvl4 = addLvl4(department?.depSections);
          let title =
              languageId == "en_US" ? department.titleEn : department.titleAr;

          departmentItem += `<li class="level-3-li">
<div class="level-3 rectangle collapsed">
<div
class="sector--title toggleParent"
data-toggle="collapse"
href="${"#collapse" + sectorIndex + "-" + depIndex}"
role="button"
aria-expanded="false"
aria-controls="${"collapse" + sectorIndex + "-" + depIndex}"
>
<span>${title}
</span>

<span
class="collapse--btn"
data-toggle="collapse"
href="${"#collapse" + sectorIndex + "-" + depIndex}"
role="button"
aria-expanded="false"
aria-controls="${"collapse" + sectorIndex + "-" + depIndex}"
>
<i class="icon-chevron-up text-white m-auto"></i>
</span>
</div>

<div class="sector--desc">
<!--<button
class="btn-sm btn more-details"
data-title="${title}" data-id="${department.divisionID
              }">${morelbl}</button>!-->
</div>
</div>
<ol
class="level-4-wrapper collapse"
id="${"collapse" + +sectorIndex + "-" + depIndex}">
${lvl4}
</ol>
</li>`;

          depIndex++;
      }

      return departmentItem;
  }

  function getDepartmentEmployees(depID, pageNum) {
      let URL = `https://hudhoor-api.adcustoms.gov.ae/dxb/${depID}/${pageNum}/${pageSize}`;
      let res= {
          "300000001664497": {
            "list": [
              {
                "fullName": "زينب خلف الزعابي",
                "personNumber": "29722",
                "phoneNumber": "5802260",
                "dateOfBirth": "1973-07-09",
                "emailAddress": "zainab.alzaabi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "فيصل عبدالله المرزوقي",
                "personNumber": "23170",
                "phoneNumber": "4452202",
                "dateOfBirth": "1978-08-29",
                "emailAddress": "faisal.almarzouqi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حامد سلطان القبيسي",
                "personNumber": "27279",
                "phoneNumber": "8008606",
                "dateOfBirth": "1980-10-27",
                "emailAddress": "hamed.alqubaisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد محمد الرميثي",
                "personNumber": "45834",
                "phoneNumber": "9333098",
                "dateOfBirth": "1989-08-11",
                "emailAddress": "ahmed.alremeithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالجليل راشد المرزوقي",
                "personNumber": "20376",
                "phoneNumber": "5663666",
                "dateOfBirth": "1970-02-11",
                "emailAddress": "AbdulJalil@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله أحمد المهيري",
                "personNumber": "47020",
                "phoneNumber": "1210069",
                "dateOfBirth": "1994-11-22",
                "emailAddress": "abdulla.almehairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد ضاحي السويدي",
                "personNumber": "20020",
                "phoneNumber": "6714545",
                "dateOfBirth": "1978-03-19",
                "emailAddress": "mohamed.alsuwaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حاضر سعيد القبيسي",
                "personNumber": "18245",
                "phoneNumber": "6123129",
                "dateOfBirth": "1968-01-01",
                "emailAddress": "hadher.saeed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمر عبدالله اليافعي",
                "personNumber": "20934",
                "phoneNumber": "4498883",
                "dateOfBirth": "1978-09-14",
                "emailAddress": "omarya@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ميرة إبراهيم الحمادي",
                "personNumber": "32942",
                "phoneNumber": "1122754",
                "dateOfBirth": "1975-11-29",
                "emailAddress": "meera.alhammadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عبدالله سعيد البلوشي",
                "personNumber": "20931",
                "phoneNumber": "3333951",
                "dateOfBirth": "1986-02-25",
                "emailAddress": "abdullah.albloshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عبيد الزعابي",
                "personNumber": "21266",
                "phoneNumber": "6606969",
                "dateOfBirth": "1984-09-11",
                "emailAddress": "mohd.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالقادر محمد الحمادي",
                "personNumber": "20604",
                "phoneNumber": "2777727",
                "dateOfBirth": "1977-07-07",
                "emailAddress": "abdelqader.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عدنان سالم الظاهري",
                "personNumber": "20625",
                "phoneNumber": "4481231",
                "dateOfBirth": "1977-10-15",
                "emailAddress": "adnan2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مريم مسلم العامري",
                "personNumber": "23471",
                "phoneNumber": "3310142",
                "dateOfBirth": "1975-07-05",
                "emailAddress": "mariam.said@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سالم بريك الكثيري",
                "personNumber": "20794",
                "phoneNumber": "4488270",
                "dateOfBirth": "1976-08-21",
                "emailAddress": "Salem_B@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد عباس الحوسني",
                "personNumber": "30870",
                "phoneNumber": "9333444",
                "dateOfBirth": "1976-11-27",
                "emailAddress": "ahmed.yasin@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "شيخة سعيد العامري",
                "personNumber": "29223",
                "phoneNumber": "8008060",
                "dateOfBirth": "1980-01-23",
                "emailAddress": "sheikha.alameri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "خليفة ضحي الهاملي",
                "personNumber": "21532",
                "phoneNumber": "6611142",
                "dateOfBirth": "1983-07-22",
                "emailAddress": "khalifad@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "نور محمد المهري",
                "personNumber": "30346",
                "phoneNumber": "1888921",
                "dateOfBirth": "1988-06-01",
                "emailAddress": "noor.almehrei@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نسرين سليمان الجابري",
                "personNumber": "30349",
                "phoneNumber": "9000505",
                "dateOfBirth": "1991-07-26",
                "emailAddress": "nesreen.sulaiman@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سعيد محمد السبوسي",
                "personNumber": "30846",
                "phoneNumber": "5525565",
                "dateOfBirth": "1984-11-17",
                "emailAddress": "saeed.alsuboosi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خليفة عبدالله المهيري",
                "personNumber": "35407",
                "phoneNumber": "9622223",
                "dateOfBirth": "1994-10-13",
                "emailAddress": "khalifa.almuhairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أنور علي البلوشي",
                "personNumber": "21513",
                "phoneNumber": "6426830",
                "dateOfBirth": "1970-05-25",
                "emailAddress": "anwar.albalushi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "موسى شفيق حديد",
                "personNumber": "23532",
                "phoneNumber": "8044855",
                "dateOfBirth": "1971-12-31",
                "emailAddress": "mousa.hadyed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد سنان المهيري",
                "personNumber": "27189",
                "phoneNumber": "6611191",
                "dateOfBirth": "1977-09-29",
                "emailAddress": "saeed.almuhairi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله محمد السويدي",
                "personNumber": "20871",
                "phoneNumber": "4420024",
                "dateOfBirth": "1970-01-03",
                "emailAddress": "Abdullas2@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 27
          },
          "300000001664542": {
            "list": [
              {
                "fullName": "ظافر عبيد القحطاني",
                "personNumber": "35041",
                "phoneNumber": "3335502",
                "dateOfBirth": "1993-12-09",
                "emailAddress": "dhafer.alqahtani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد خليفة القبيسي",
                "personNumber": "29346",
                "phoneNumber": "4454455",
                "dateOfBirth": "1980-06-23",
                "emailAddress": "mohammed.alqubaisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد عبدالله النيادي",
                "personNumber": "20808",
                "phoneNumber": "6115009",
                "dateOfBirth": "1981-02-21",
                "emailAddress": "salneyadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناجية جمعة البحراني",
                "personNumber": "28482",
                "phoneNumber": "7133376",
                "dateOfBirth": "1977-01-01",
                "emailAddress": "najia.albahrani@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "صالح محمد الشامسي",
                "personNumber": "20494",
                "phoneNumber": "4474493",
                "dateOfBirth": "1977-04-23",
                "emailAddress": "saleh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يوسف سيف النيادي",
                "personNumber": "20641",
                "phoneNumber": "8008322",
                "dateOfBirth": "1980-05-31",
                "emailAddress": "yosef.alneyadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي سيف البلوشي",
                "personNumber": "39835",
                "phoneNumber": "1366818",
                "dateOfBirth": "1991-11-26",
                "emailAddress": "ali.albaloushi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله سالم الساعدي",
                "personNumber": "20916",
                "phoneNumber": "6661514",
                "dateOfBirth": "1984-04-03",
                "emailAddress": "doc001938@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فاخرة سيف الكعبي",
                "personNumber": "29148",
                "phoneNumber": "3666451",
                "dateOfBirth": "1985-10-29",
                "emailAddress": "fakira.alkabi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سيف مبارك النعيمي",
                "personNumber": "34000",
                "phoneNumber": "5553155",
                "dateOfBirth": "1993-08-23",
                "emailAddress": "saif.alnuaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ظافر حمد الأحبابي",
                "personNumber": "35039",
                "phoneNumber": "4455118",
                "dateOfBirth": "1994-05-11",
                "emailAddress": "dhafer.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عائشة عوض الظاهري",
                "personNumber": "30121",
                "phoneNumber": "9311172",
                "dateOfBirth": "1985-12-31",
                "emailAddress": "aisha.aldheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عبدالله علي الشامسي",
                "personNumber": "20624",
                "phoneNumber": "3333849",
                "dateOfBirth": "1979-09-06",
                "emailAddress": "AbdullahA@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عشق محمد الأحبابي",
                "personNumber": "27933",
                "phoneNumber": "2738887",
                "dateOfBirth": "1989-07-01",
                "emailAddress": "aseq.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سهيل العامري",
                "personNumber": "20807",
                "phoneNumber": "4480099",
                "dateOfBirth": "1983-04-03",
                "emailAddress": "Mohammed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمدة محمد الكعبي",
                "personNumber": "33016",
                "phoneNumber": "7337144",
                "dateOfBirth": "1983-08-29",
                "emailAddress": "hamda.alkabi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "هزاع خليفة الفارسي",
                "personNumber": "24256",
                "phoneNumber": "5226666",
                "dateOfBirth": "1985-12-19",
                "emailAddress": "hazaa.alfarsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ذياب حمود الهاجري",
                "personNumber": "25502",
                "phoneNumber": "5961113",
                "dateOfBirth": "1983-09-23",
                "emailAddress": "thaib.hajiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد إبراهيم عبدالله",
                "personNumber": "25512",
                "phoneNumber": "1000496",
                "dateOfBirth": "1988-06-07",
                "emailAddress": "mohamed.mohammed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالرحمن محمد البلوشي",
                "personNumber": "29224",
                "phoneNumber": "2666655",
                "dateOfBirth": "1986-09-21",
                "emailAddress": "abdulrahman.albloshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حميد صالح المنصوري",
                "personNumber": "47624",
                "phoneNumber": null,
                "dateOfBirth": "1991-09-28",
                "emailAddress": "hs.almansoori@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "إبراهيم محمد البلوشي",
                "personNumber": "29787",
                "phoneNumber": "3334172",
                "dateOfBirth": "1990-02-05",
                "emailAddress": "ibrahim.albloushi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ريم عمر العامري",
                "personNumber": "30778",
                "phoneNumber": "4499180",
                "dateOfBirth": "1986-08-04",
                "emailAddress": "reem.rajab@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "ليلى عتيق النعيمي",
                "personNumber": "27937",
                "phoneNumber": "5022213",
                "dateOfBirth": "1986-10-04",
                "emailAddress": "laila.alnoimi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حمدان خليفة الغيثي",
                "personNumber": "47192",
                "phoneNumber": "9966727",
                "dateOfBirth": "1995-05-26",
                "emailAddress": "hamdan.alghaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد سلطان الظاهري",
                "personNumber": "34007",
                "phoneNumber": "1372111",
                "dateOfBirth": "1989-07-13",
                "emailAddress": "saeed.aldhaheri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد حسن الأحبابي",
                "personNumber": "35027",
                "phoneNumber": "1123355",
                "dateOfBirth": "1991-09-22",
                "emailAddress": "saeed.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد ظبيب الشامسي",
                "personNumber": "20907",
                "phoneNumber": "5130999",
                "dateOfBirth": "1978-03-29",
                "emailAddress": "doc001913@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "دهم سعيد الأحبابي",
                "personNumber": "35056",
                "phoneNumber": "8080905",
                "dateOfBirth": "1993-12-10",
                "emailAddress": "daham.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد حمد الشرياني",
                "personNumber": "30122",
                "phoneNumber": "3332995",
                "dateOfBirth": "1990-12-03",
                "emailAddress": "ahmad.alsheriani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد بطي الغفلي",
                "personNumber": "20813",
                "phoneNumber": "5859555",
                "dateOfBirth": "1978-01-31",
                "emailAddress": "rashedg@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم سعيد النعيمي",
                "personNumber": "20920",
                "phoneNumber": "4473441",
                "dateOfBirth": "1986-08-25",
                "emailAddress": "salemnum@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد عبدالله المهري",
                "personNumber": "29173",
                "phoneNumber": "5337330",
                "dateOfBirth": "1986-09-11",
                "emailAddress": "hamad.almohaire@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ياسر سعيد الكعبي",
                "personNumber": "28485",
                "phoneNumber": "8388588",
                "dateOfBirth": "1984-07-14",
                "emailAddress": "yaser.alkaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "إبراهيم مراد البلوشي",
                "personNumber": "33999",
                "phoneNumber": "7443399",
                "dateOfBirth": "1975-01-01",
                "emailAddress": "ibrahim.albulooshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سيف نهيل الكتبي",
                "personNumber": "33591",
                "phoneNumber": "9393212",
                "dateOfBirth": "1991-09-12",
                "emailAddress": "saif.alketbi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عضيمان حسن الأحبابي",
                "personNumber": "35017",
                "phoneNumber": "6656606",
                "dateOfBirth": "1993-02-13",
                "emailAddress": "odaiman.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر محمد المنصوري",
                "personNumber": "28695",
                "phoneNumber": "6661120",
                "dateOfBirth": "1981-09-07",
                "emailAddress": "naser.almansori@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عمير الظاهري",
                "personNumber": "20909",
                "phoneNumber": "3311553",
                "dateOfBirth": "1982-04-30",
                "emailAddress": "doc001945@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مها هادف الشامسي",
                "personNumber": "47649",
                "phoneNumber": null,
                "dateOfBirth": "2000-07-21",
                "emailAddress": "maha.alshamsi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نوال غمران الظاهري",
                "personNumber": "43146",
                "phoneNumber": "1382857",
                "dateOfBirth": "1988-08-27",
                "emailAddress": "nawal.aldhaheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نورة حسن البلوشى",
                "personNumber": "30123",
                "phoneNumber": "9494995",
                "dateOfBirth": "1989-01-29",
                "emailAddress": "nora.albeloshi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حمدان محمد المعمري",
                "personNumber": "29571",
                "phoneNumber": "7111764",
                "dateOfBirth": "1988-10-27",
                "emailAddress": "hamdan.almeamari@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد سهيل النعيمي",
                "personNumber": "40021",
                "phoneNumber": "3330940",
                "dateOfBirth": "1986-11-10",
                "emailAddress": "khaled.alnuaimi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان سهيل النعيمي",
                "personNumber": "20900",
                "phoneNumber": "6233861",
                "dateOfBirth": "1983-02-19",
                "emailAddress": "sultan.suhail@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "موزة سعيد الشبلي",
                "personNumber": "27209",
                "phoneNumber": "6008434",
                "dateOfBirth": "1980-02-26",
                "emailAddress": "muza.alshebli@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "طارق عوض الجابري",
                "personNumber": "20605",
                "phoneNumber": "4480608",
                "dateOfBirth": "1975-10-07",
                "emailAddress": "tareqj@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "هند عبدالله الشامسي",
                "personNumber": "29555",
                "phoneNumber": "8314444",
                "dateOfBirth": "1988-03-02",
                "emailAddress": "hind.alshamsi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "فاطمة حمود القبيسي",
                "personNumber": "30252",
                "phoneNumber": "7999190",
                "dateOfBirth": "1986-01-07",
                "emailAddress": "fatima.alqubaisi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "وضاح محمد الحوقاني",
                "personNumber": "29134",
                "phoneNumber": "6684855",
                "dateOfBirth": "1984-11-10",
                "emailAddress": "waddah.houqani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "آمنة الدحبة العامري",
                "personNumber": "47650",
                "phoneNumber": null,
                "dateOfBirth": "1994-05-24",
                "emailAddress": "amna.alameri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سالم عبيد الشامسي",
                "personNumber": "26901",
                "phoneNumber": "8008737",
                "dateOfBirth": "1986-09-22",
                "emailAddress": "salem.alshamsi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "هزاع صابر البلوشي",
                "personNumber": "39795",
                "phoneNumber": "3393535",
                "dateOfBirth": "1987-11-27",
                "emailAddress": "hazza.albaloushi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله راشد الشامسي",
                "personNumber": "47143",
                "phoneNumber": "9854443",
                "dateOfBirth": "1994-07-08",
                "emailAddress": "abdulla.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان سالم الكلباني",
                "personNumber": "34018",
                "phoneNumber": "6649997",
                "dateOfBirth": "1985-02-02",
                "emailAddress": "sultan.alkalbani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم راشد الشامسي",
                "personNumber": "39990",
                "phoneNumber": "1102666",
                "dateOfBirth": "1992-07-04",
                "emailAddress": "salem.alshamesi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سعيد الشامسي",
                "personNumber": "20971",
                "phoneNumber": "6933783",
                "dateOfBirth": "1986-12-21",
                "emailAddress": "mohd.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد محمد الشامسي",
                "personNumber": "24254",
                "phoneNumber": "8086666",
                "dateOfBirth": "1987-09-26",
                "emailAddress": "saeed.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعود حسن الشحي",
                "personNumber": "29805",
                "phoneNumber": "7333866",
                "dateOfBirth": "1983-11-16",
                "emailAddress": "saoud.alshehhi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "صالح حسين المنصوري",
                "personNumber": "40026",
                "phoneNumber": "8999966",
                "dateOfBirth": "1994-04-18",
                "emailAddress": "saleh.almansoori@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد الراعي الشامسي",
                "personNumber": "27213",
                "phoneNumber": "1122599",
                "dateOfBirth": "1989-02-15",
                "emailAddress": "mohammed.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد ماجد الظاهري",
                "personNumber": "28461",
                "phoneNumber": "3020905",
                "dateOfBirth": "1987-02-25",
                "emailAddress": "hamad.aldhahery@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سيف علي الشامسي",
                "personNumber": "40024",
                "phoneNumber": "9633050",
                "dateOfBirth": "1994-10-07",
                "emailAddress": "saif.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سعيد الشامسي",
                "personNumber": "32996",
                "phoneNumber": "4805999",
                "dateOfBirth": "1987-05-27",
                "emailAddress": "mohammed.alshamsi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "وفاء علي البلوشي",
                "personNumber": "47030",
                "phoneNumber": "2626270",
                "dateOfBirth": "1979-04-29",
                "emailAddress": "wafa.albaloushi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "علي راشد الكعبي",
                "personNumber": "30277",
                "phoneNumber": "2294444",
                "dateOfBirth": "1991-07-02",
                "emailAddress": "Ali.alkaaebi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راضي محمد دحباج",
                "personNumber": "24321",
                "phoneNumber": "5591001",
                "dateOfBirth": "1981-07-02",
                "emailAddress": "radi.dhbaj@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد أحمد البلوشي",
                "personNumber": "29158",
                "phoneNumber": "7998877",
                "dateOfBirth": "1982-05-04",
                "emailAddress": "rashed.albaloshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي راشد المزروعي",
                "personNumber": "39992",
                "phoneNumber": "6648885",
                "dateOfBirth": "1986-11-26",
                "emailAddress": "ali.almazrouei@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فاطمة محمد النعيمي",
                "personNumber": "28644",
                "phoneNumber": "8389977",
                "dateOfBirth": "1973-04-09",
                "emailAddress": "fatima.alnuaimi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عبدالله مقبل العامري",
                "personNumber": "32992",
                "phoneNumber": "6669222",
                "dateOfBirth": "1987-07-29",
                "emailAddress": "abdulla.alameri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حميد عبدالله المزروعي",
                "personNumber": "34011",
                "phoneNumber": "9000307",
                "dateOfBirth": "1992-12-31",
                "emailAddress": "humaid.almazroui@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد عبدالله الشامسي",
                "personNumber": "29786",
                "phoneNumber": "8309033",
                "dateOfBirth": "1992-01-08",
                "emailAddress": "saeed.alshamsi4@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "جابر زيد التميمي",
                "personNumber": "28400",
                "phoneNumber": "3339696",
                "dateOfBirth": "1988-07-01",
                "emailAddress": "jaber.altamimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمار عبدالله البلوشي",
                "personNumber": "28577",
                "phoneNumber": "6730111",
                "dateOfBirth": "1987-02-26",
                "emailAddress": "ammar.albaloshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان سالم الشامسي",
                "personNumber": "24276",
                "phoneNumber": "3307887",
                "dateOfBirth": "1982-10-25",
                "emailAddress": "sultan.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سحمي هادي الأحبابي",
                "personNumber": "20485",
                "phoneNumber": "050-6185411",
                "dateOfBirth": "1974-07-01",
                "emailAddress": "Sahmi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فيصل سعيد البلوشي",
                "personNumber": "20972",
                "phoneNumber": "8000395",
                "dateOfBirth": "1982-05-18",
                "emailAddress": "faisal.alboloshi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم إبراهيم الظاهري",
                "personNumber": "20781",
                "phoneNumber": "8127171",
                "dateOfBirth": "1984-04-22",
                "emailAddress": "Salem_I@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حميد عبدالله الظاهري",
                "personNumber": "26910",
                "phoneNumber": "5100103",
                "dateOfBirth": "1986-11-17",
                "emailAddress": "humaid.aldhaheri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فاطمة خميس الكعبي",
                "personNumber": "25496",
                "phoneNumber": "2220069",
                "dateOfBirth": "1975-03-30",
                "emailAddress": "fatima.alkaabi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "مريم مطر البلوشي",
                "personNumber": "30800",
                "phoneNumber": "4871119",
                "dateOfBirth": "1986-06-27",
                "emailAddress": "mariam.alblooshi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حمد عبدالله المعمري",
                "personNumber": "29534",
                "phoneNumber": "3333392",
                "dateOfBirth": "1991-03-31",
                "emailAddress": "hamad.almamary@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد محمد العزيزي",
                "personNumber": "32190",
                "phoneNumber": "1130305",
                "dateOfBirth": "1988-09-19",
                "emailAddress": "khaled.hareb@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان سالم الزعابي",
                "personNumber": "24286",
                "phoneNumber": "9020101",
                "dateOfBirth": "1980-01-28",
                "emailAddress": "sultan.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مريم محمد الناصري",
                "personNumber": "29554",
                "phoneNumber": "4481131",
                "dateOfBirth": "1984-10-16",
                "emailAddress": "maryam.alnasseri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حميد راشد الشامسي",
                "personNumber": "21034",
                "phoneNumber": "6231211",
                "dateOfBirth": "1980-02-22",
                "emailAddress": "humaid.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "شيخة علي العرجاني",
                "personNumber": "47651",
                "phoneNumber": null,
                "dateOfBirth": "1992-02-17",
                "emailAddress": "shaikha.alarjani@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سعيد عبيد النيادي",
                "personNumber": "20899",
                "phoneNumber": "7300017",
                "dateOfBirth": "1983-11-07",
                "emailAddress": "saeedn@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حصة علي النيادي",
                "personNumber": "30222",
                "phoneNumber": "8487555",
                "dateOfBirth": "1986-11-22",
                "emailAddress": "hessa.alneyadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "إبراهيم أحمد الطنيجي",
                "personNumber": "21016",
                "phoneNumber": "6388825",
                "dateOfBirth": "1985-01-07",
                "emailAddress": "ibrahim.alshehi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد خليفة الظاهري",
                "personNumber": "27241",
                "phoneNumber": "5555243",
                "dateOfBirth": "1987-10-22",
                "emailAddress": "saeed.aldhaheri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد راشد الأحبابي",
                "personNumber": "34020",
                "phoneNumber": "7776626",
                "dateOfBirth": "1993-05-01",
                "emailAddress": "mohammed.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمران علي الغفلي",
                "personNumber": "29175",
                "phoneNumber": "3777779",
                "dateOfBirth": "1987-05-12",
                "emailAddress": "omran.alghafli@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مسفر عايض الأحبابي",
                "personNumber": "35110",
                "phoneNumber": "2222510",
                "dateOfBirth": "1992-10-25",
                "emailAddress": "mesfer.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد أحمد الظاهري",
                "personNumber": "35020",
                "phoneNumber": "3336226",
                "dateOfBirth": "1992-06-30",
                "emailAddress": "hamad.aldhaheri3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ميثا عبدالله السويدي",
                "personNumber": "46601",
                "phoneNumber": "7333115",
                "dateOfBirth": "1984-08-13",
                "emailAddress": "maitha.alsuwaidi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سيف عبدالله الكويتي",
                "personNumber": "29806",
                "phoneNumber": "7344420",
                "dateOfBirth": "1989-09-22",
                "emailAddress": "saif.alkuwaiti@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "إبراهيم سالم الساعدي",
                "personNumber": "35032",
                "phoneNumber": "1713137",
                "dateOfBirth": "1991-10-18",
                "emailAddress": "ibrahim.alsaedi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد سلطان الشامسي",
                "personNumber": "35042",
                "phoneNumber": "3332012",
                "dateOfBirth": "1993-08-12",
                "emailAddress": "khaled.alshamisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فهد محمد النيادي",
                "personNumber": "20806",
                "phoneNumber": "6666514",
                "dateOfBirth": "1983-01-04",
                "emailAddress": "fahd.sa@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "رقية محمد خوري",
                "personNumber": "26871",
                "phoneNumber": "3305502",
                "dateOfBirth": "1983-06-25",
                "emailAddress": "roqaia.abdullah@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "محمد سلطان الكتبي",
                "personNumber": "33996",
                "phoneNumber": "3346655",
                "dateOfBirth": "1992-03-15",
                "emailAddress": "mohammed.alketbi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عزة سالم النيادي",
                "personNumber": "47121",
                "phoneNumber": "8444669",
                "dateOfBirth": "1974-12-24",
                "emailAddress": "azza.alneyadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سيف مسفر الكربي",
                "personNumber": "26958",
                "phoneNumber": "7331515",
                "dateOfBirth": "1988-05-23",
                "emailAddress": "saif.alkarbi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد خليفة الشامسي",
                "personNumber": "27281",
                "phoneNumber": "6196311",
                "dateOfBirth": "1978-12-15",
                "emailAddress": "saeed.alshamsi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالرحمن عبدالله النعيمي",
                "personNumber": "39986",
                "phoneNumber": "1199356",
                "dateOfBirth": "1993-03-08",
                "emailAddress": "abdulrahman.alnuaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمر سعيد الشامسي",
                "personNumber": "30278",
                "phoneNumber": "3333136",
                "dateOfBirth": "1987-01-21",
                "emailAddress": "omar.alshamsi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مانع سالم الشامسي",
                "personNumber": "20778",
                "phoneNumber": "7337888",
                "dateOfBirth": "1982-04-24",
                "emailAddress": "Manie@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي عبدالله النعيمي",
                "personNumber": "40019",
                "phoneNumber": "6665095",
                "dateOfBirth": "1991-04-30",
                "emailAddress": "ali.alnuaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فهد راشد المري",
                "personNumber": "33615",
                "phoneNumber": "3331920",
                "dateOfBirth": "1991-06-19",
                "emailAddress": "rashid.almeri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله صالح العامري",
                "personNumber": "26714",
                "phoneNumber": "6677715",
                "dateOfBirth": "1981-09-04",
                "emailAddress": "abdulla.alameri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "شمسة دريول الظاهري",
                "personNumber": "27922",
                "phoneNumber": "4333911",
                "dateOfBirth": "1985-05-01",
                "emailAddress": "shamsa.aldhaheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سلطان محمد النعيمي",
                "personNumber": "20666",
                "phoneNumber": "6660933",
                "dateOfBirth": "1979-09-12",
                "emailAddress": "sultan.alnuaime@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حريز أحمد حريز",
                "personNumber": "24275",
                "phoneNumber": "8546669",
                "dateOfBirth": "1984-02-10",
                "emailAddress": "hereez.amer@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عامر الكويتي",
                "personNumber": "20810",
                "phoneNumber": "1315158",
                "dateOfBirth": "1983-10-22",
                "emailAddress": "mohd.kuwaiti@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عيسى عوض السالمي",
                "personNumber": "29159",
                "phoneNumber": "7488894",
                "dateOfBirth": "1982-07-25",
                "emailAddress": "eisa.alsalmi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم فارس الشامسي",
                "personNumber": "20612",
                "phoneNumber": "4936665",
                "dateOfBirth": "1979-10-29",
                "emailAddress": "salemfa@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي محمد الأحبابي",
                "personNumber": "35064",
                "phoneNumber": "8000415",
                "dateOfBirth": "1994-07-22",
                "emailAddress": "ali.alahbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سلطان العرياني",
                "personNumber": "27280",
                "phoneNumber": "3337111",
                "dateOfBirth": "1984-04-28",
                "emailAddress": "mohamed.aleriani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "غريبة حريز الراشدي",
                "personNumber": "29424",
                "phoneNumber": "3344489",
                "dateOfBirth": "1981-04-14",
                "emailAddress": "ghariba.alrashdi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "خالد سالم النعيمي",
                "personNumber": "33576",
                "phoneNumber": "7844433",
                "dateOfBirth": "1992-08-18",
                "emailAddress": "khaled.alnuaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد ماجد الظاهري",
                "personNumber": "20802",
                "phoneNumber": "6116650",
                "dateOfBirth": "1976-11-11",
                "emailAddress": "Ahmed_M@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فاطمة محمد الناصري",
                "personNumber": "28065",
                "phoneNumber": "4040489",
                "dateOfBirth": "1970-02-16",
                "emailAddress": "fatima.alnasseri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "علي راشد الكندي",
                "personNumber": "20784",
                "phoneNumber": "7631133",
                "dateOfBirth": "1981-09-15",
                "emailAddress": "Ali_R@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "منصور راشد المري",
                "personNumber": "43138",
                "phoneNumber": "2008290",
                "dateOfBirth": "1993-07-28",
                "emailAddress": "mansour.almerri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "رجاء سعيد النعيمي",
                "personNumber": "27218",
                "phoneNumber": "1230030",
                "dateOfBirth": "1980-08-16",
                "emailAddress": "rajaa.alnuaimi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "لطيفة خليفة النيادي",
                "personNumber": "23473",
                "phoneNumber": "2313115",
                "dateOfBirth": "1974-03-23",
                "emailAddress": "latifa.alneiadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سعيد هاشل النعيمي",
                "personNumber": "20689",
                "phoneNumber": "6198557",
                "dateOfBirth": "1982-04-27",
                "emailAddress": "salnuaime@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عائشة سالم الشامسي",
                "personNumber": "28398",
                "phoneNumber": "9908022",
                "dateOfBirth": "1983-05-16",
                "emailAddress": "aisha.alshamsi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أمل سعيد الشبلي",
                "personNumber": "27208",
                "phoneNumber": "9944452",
                "dateOfBirth": "1984-12-14",
                "emailAddress": "amal.alshebli@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "منى ناصر البفته",
                "personNumber": "29153",
                "phoneNumber": "6666553",
                "dateOfBirth": "1983-12-29",
                "emailAddress": "muna.albafta@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نورة محمد البلوشي",
                "personNumber": "27411",
                "phoneNumber": "6443283",
                "dateOfBirth": "1984-02-04",
                "emailAddress": "noura.albloushi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "آمنة محمد الظاهري",
                "personNumber": "25498",
                "phoneNumber": "5331139",
                "dateOfBirth": "1977-12-04",
                "emailAddress": "amna.aldhahiri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "فاطمة العصري الظاهري",
                "personNumber": "30793",
                "phoneNumber": "3352423",
                "dateOfBirth": "1984-12-02",
                "emailAddress": "fatima.aldhaheri2@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حمد مسعود الأحبابي",
                "personNumber": "26567",
                "phoneNumber": "8193333",
                "dateOfBirth": "1978-05-12",
                "emailAddress": "hamad.alahbabi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبيد جاسم الظاهري",
                "personNumber": "20699",
                "phoneNumber": "5880300",
                "dateOfBirth": "1982-10-04",
                "emailAddress": "obaidj@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله أحمد المهري",
                "personNumber": "43147",
                "phoneNumber": "5171760",
                "dateOfBirth": "1992-06-21",
                "emailAddress": "Abdulla.almahri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد ناصر المنصوري",
                "personNumber": "21240",
                "phoneNumber": "4614334",
                "dateOfBirth": "1966-12-24",
                "emailAddress": "saeed.almansouri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فاطمة مصبح النعيمي",
                "personNumber": "21004",
                "phoneNumber": "4930998",
                "dateOfBirth": "1982-08-22",
                "emailAddress": "fatimam@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نعيمة محمد البلوشي",
                "personNumber": "29332",
                "phoneNumber": "5333853",
                "dateOfBirth": "1978-11-11",
                "emailAddress": "naeema.albuloshi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "خولة حمد الشرياني",
                "personNumber": "20702",
                "phoneNumber": "6000688",
                "dateOfBirth": "1978-07-30",
                "emailAddress": "khawla_alsheriany@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أميرة حمود القبيسي",
                "personNumber": "25513",
                "phoneNumber": "3311377",
                "dateOfBirth": "1982-09-21",
                "emailAddress": "ameera.alqubaisi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "مريم صالح الوحشي",
                "personNumber": "25500",
                "phoneNumber": "9606993",
                "dateOfBirth": "1988-02-16",
                "emailAddress": "maryam.alwahshi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حكمة سالم البادي",
                "personNumber": "30875",
                "phoneNumber": "4729636",
                "dateOfBirth": "1977-04-09",
                "emailAddress": "hikma.albadi@adcustoms.gov.ae",
                "gender": "Female"
              }
            ],
            "total": 145
          },
          "300000001664317": {
            "list": [
              {
                "fullName": "عنود عيد الظاهري",
                "personNumber": "30118",
                "phoneNumber": "1121031",
                "dateOfBirth": "1991-01-14",
                "emailAddress": "Anoud.aldhaheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "محمد أحمد العبيدلي",
                "personNumber": "30223",
                "phoneNumber": "9696296",
                "dateOfBirth": "1988-02-21",
                "emailAddress": "m.alobeidli@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "العنود مسلم العامري",
                "personNumber": "47584",
                "phoneNumber": "3553160",
                "dateOfBirth": "1994-08-30",
                "emailAddress": "doc47584@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "محمد عبدالله الخوري",
                "personNumber": "20851",
                "phoneNumber": "6616868",
                "dateOfBirth": "1984-02-17",
                "emailAddress": "mohd.khoori@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مصطفى سامي الهاشمي",
                "personNumber": "33013",
                "phoneNumber": "1020040",
                "dateOfBirth": "1987-08-08",
                "emailAddress": "mustafa.sahil@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ميعاد مراد البلوشي",
                "personNumber": "28625",
                "phoneNumber": "6738000",
                "dateOfBirth": "1986-01-27",
                "emailAddress": "mead.albloshi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "شمة حسين الصيعري",
                "personNumber": "47586",
                "phoneNumber": "8144777",
                "dateOfBirth": "1986-01-08",
                "emailAddress": "shamma.alseiari@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حمامة مبارك المنصوري",
                "personNumber": "43165",
                "phoneNumber": "4177760",
                "dateOfBirth": "1981-08-26",
                "emailAddress": "hamama.almansoori@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نجاة جمعان المرعي",
                "personNumber": "21047",
                "phoneNumber": "1264764",
                "dateOfBirth": "1986-10-24",
                "emailAddress": "najat@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سلامة سعيد المنصوري",
                "personNumber": "47560",
                "phoneNumber": "3323247",
                "dateOfBirth": "1985-08-12",
                "emailAddress": "Salama.almansoori@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أحمد سلطان الزعابي",
                "personNumber": "20646",
                "phoneNumber": "6420009",
                "dateOfBirth": "1981-06-21",
                "emailAddress": "ahmedz@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 11
          },
          "300000001664377": {
            "list": [
              {
                "fullName": "أحمد خلفان الرميثي",
                "personNumber": "20069",
                "phoneNumber": "6422636",
                "dateOfBirth": "1973-11-25",
                "emailAddress": "AKALRUMAITHI@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عبدالله حسن",
                "personNumber": "30155",
                "phoneNumber": "4439917",
                "dateOfBirth": "1980-11-02",
                "emailAddress": "mohamed.ali2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ريم عتيق الرميثي",
                "personNumber": "27206",
                "phoneNumber": "6608812",
                "dateOfBirth": "1987-02-11",
                "emailAddress": "reem.alromaithi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "إبراهيم سالم الطنيجي",
                "personNumber": "20742",
                "phoneNumber": "3100098",
                "dateOfBirth": "1983-05-07",
                "emailAddress": "ibraheem.altenaiji@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "شما علي الواحدي",
                "personNumber": "47606",
                "phoneNumber": "6767990",
                "dateOfBirth": "2000-03-26",
                "emailAddress": "shama.alwahedi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "شاهول حميد عبدالقادر",
                "personNumber": "47619",
                "phoneNumber": null,
                "dateOfBirth": "1966-11-04",
                "emailAddress": "shahul.hameed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "دانة راشد الزعابي",
                "personNumber": "46505",
                "phoneNumber": "3221113",
                "dateOfBirth": "1990-07-16",
                "emailAddress": "dana.alzaabi@adcustoms.gov.ae",
                "gender": "Female"
              }
            ],
            "total": 7
          },
          "300000001664309": {
            "list": [
              {
                "fullName": "منصور حاجي مالك",
                "personNumber": "22703",
                "phoneNumber": "5660300",
                "dateOfBirth": "1962-01-16",
                "emailAddress": "mansoor.malek@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم سويعد الهطالي",
                "personNumber": "47579",
                "phoneNumber": "3160601",
                "dateOfBirth": "1986-06-17",
                "emailAddress": "salem.alhattali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مبارك راشد المنصوري",
                "personNumber": "20520",
                "phoneNumber": "8008044",
                "dateOfBirth": "1975-09-10",
                "emailAddress": "mubarakm2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عائشة علي العبري",
                "personNumber": "47620",
                "phoneNumber": null,
                "dateOfBirth": "1994-03-12",
                "emailAddress": "Ayesha.AlEbri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "موزة عبدالله الجابري",
                "personNumber": "27429",
                "phoneNumber": "6730987",
                "dateOfBirth": "1982-01-13",
                "emailAddress": "moza.aljaberi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "سعود صالح الحارثي",
                "personNumber": "30341",
                "phoneNumber": "1821825",
                "dateOfBirth": "1985-09-28",
                "emailAddress": "saoud.alharthi@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 6
          },
          "300000001664389": {
            "list": [
              {
                "fullName": "خالد سالم اليحمدي",
                "personNumber": "47628",
                "phoneNumber": null,
                "dateOfBirth": "1991-09-05",
                "emailAddress": "khaled.alyahmadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "نورة خميس الرميثي",
                "personNumber": "25493",
                "phoneNumber": "5621080",
                "dateOfBirth": "1985-07-12",
                "emailAddress": "noura.alromaithi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "ناجي محمد المنصوري",
                "personNumber": "20981",
                "phoneNumber": "6113974",
                "dateOfBirth": "1972-02-02",
                "emailAddress": "najim@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 3
          },
          "300000001664379": {
            "list": [
              {
                "fullName": "علي عتيق الهاملي",
                "personNumber": "20733",
                "phoneNumber": "8145454",
                "dateOfBirth": "1979-08-22",
                "emailAddress": "Ali_A@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر خميس المنصوري",
                "personNumber": "20500",
                "phoneNumber": "5219997",
                "dateOfBirth": "1976-07-01",
                "emailAddress": "nasser.almansouri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "منذر محمد الشعيبي",
                "personNumber": "20629",
                "phoneNumber": "8008866",
                "dateOfBirth": "1977-11-28",
                "emailAddress": "monther.alshaibi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خادم راشد المهيري",
                "personNumber": "18611",
                "phoneNumber": "6111820",
                "dateOfBirth": "1979-03-17",
                "emailAddress": "khadem.almehairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد حمدان الخميري",
                "personNumber": "20672",
                "phoneNumber": "4450050",
                "dateOfBirth": "1980-02-28",
                "emailAddress": "rashed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد عبدالله الطنيجي",
                "personNumber": "22856",
                "phoneNumber": "8133200",
                "dateOfBirth": "1972-12-14",
                "emailAddress": "ALTenaiji@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عامر محمد العامري",
                "personNumber": "33981",
                "phoneNumber": "3235999",
                "dateOfBirth": "1991-07-01",
                "emailAddress": "Amer.alameri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان محمد المنصوري",
                "personNumber": "30777",
                "phoneNumber": "6649477",
                "dateOfBirth": "1988-06-20",
                "emailAddress": "sultan.ali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أبوبكر عيدروس الجيلاني",
                "personNumber": "34038",
                "phoneNumber": "2355589",
                "dateOfBirth": "1991-01-10",
                "emailAddress": "abubaker.aljailani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سليم العامري",
                "personNumber": "25505",
                "phoneNumber": "3380808",
                "dateOfBirth": "1986-04-11",
                "emailAddress": "mohammad.alamri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ارحمه إبراهيم الزعابي",
                "personNumber": "34025",
                "phoneNumber": "8155007",
                "dateOfBirth": "1992-09-12",
                "emailAddress": "arhama.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عيسى بخيت المنصوري",
                "personNumber": "46081",
                "phoneNumber": "5112711",
                "dateOfBirth": "1986-07-28",
                "emailAddress": "essa.almansoori@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "همام سالم آل علي",
                "personNumber": "30208",
                "phoneNumber": "9000152",
                "dateOfBirth": "1989-10-03",
                "emailAddress": "hammam.alali@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 13
          },
          "300000001664419": {
            "list": [
              {
                "fullName": "موزة خلفان النيادي",
                "personNumber": "35015",
                "phoneNumber": "8195353",
                "dateOfBirth": "1988-11-25",
                "emailAddress": "mouza.alneyadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أحمد حسن الزعابي",
                "personNumber": "20506",
                "phoneNumber": "5668656",
                "dateOfBirth": "1976-02-27",
                "emailAddress": "aalzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مبارك مطر المنصوري",
                "personNumber": "20426",
                "phoneNumber": null,
                "dateOfBirth": "1973-03-03",
                "emailAddress": "matar@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد حميد الظاهري",
                "personNumber": "27195",
                "phoneNumber": "6437111",
                "dateOfBirth": "1973-08-21",
                "emailAddress": "ahmad.aldaheri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمران فالح النعيمي",
                "personNumber": "20614",
                "phoneNumber": "4477113",
                "dateOfBirth": "1980-01-09",
                "emailAddress": "omran.alnuaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يوسف سليمان الشحي",
                "personNumber": "23438",
                "phoneNumber": "6626050",
                "dateOfBirth": "1976-05-05",
                "emailAddress": "yousef.sulaiman@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد حسن الحوسني",
                "personNumber": "20490",
                "phoneNumber": "7778555",
                "dateOfBirth": "1974-12-29",
                "emailAddress": "ralhosany@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يونس فتح الخاجة",
                "personNumber": "21466",
                "phoneNumber": "4411569",
                "dateOfBirth": "1965-01-01",
                "emailAddress": "yunisF@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد حياب الكتبي",
                "personNumber": "20633",
                "phoneNumber": "6188895",
                "dateOfBirth": "1975-05-29",
                "emailAddress": "mhd163@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مطر شليويح المنصوري",
                "personNumber": "23324",
                "phoneNumber": "6123428",
                "dateOfBirth": "1975-10-27",
                "emailAddress": "matar.almansouri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم عيسى الحمادي",
                "personNumber": "20345",
                "phoneNumber": "8008808",
                "dateOfBirth": "1964-12-27",
                "emailAddress": "saleme@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي عبدالله الكويتي",
                "personNumber": "20545",
                "phoneNumber": "4447077",
                "dateOfBirth": "1975-08-31",
                "emailAddress": "ali2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "بطي سالم القبيسي",
                "personNumber": "23578",
                "phoneNumber": "6155566",
                "dateOfBirth": "1974-02-28",
                "emailAddress": "butti.alqubaisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد سعد القحطاني",
                "personNumber": "20905",
                "phoneNumber": "8115257",
                "dateOfBirth": "1977-01-01",
                "emailAddress": "salquhtani@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 14
          },
          "300000001664592": {
            "list": [
              {
                "fullName": "نوف عبدالحميد المستكاء",
                "personNumber": "33010",
                "phoneNumber": "5336808",
                "dateOfBirth": "1990-10-08",
                "emailAddress": "noof.mohammed@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عدنان محمد الزعابي",
                "personNumber": "20364",
                "phoneNumber": "6116136",
                "dateOfBirth": "1967-01-01",
                "emailAddress": "adnan@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "انهية محمد المري",
                "personNumber": "24456",
                "phoneNumber": "8882613",
                "dateOfBirth": "1978-07-17",
                "emailAddress": "anheyam@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "وليد بخيت الفلاحي",
                "personNumber": "47568",
                "phoneNumber": "2313000",
                "dateOfBirth": "1981-01-07",
                "emailAddress": "waleed.alfalahi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خولة عبيد العتيبة",
                "personNumber": "26852",
                "phoneNumber": "8303322",
                "dateOfBirth": "1980-02-02",
                "emailAddress": "khawla.alotaiba@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "ميره سعيد الشحي",
                "personNumber": "47610",
                "phoneNumber": "8687575",
                "dateOfBirth": "1996-11-14",
                "emailAddress": "meera.alshehhi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "فهد  محمد ال علي",
                "personNumber": "47609",
                "phoneNumber": "8787895",
                "dateOfBirth": "1984-11-27",
                "emailAddress": "fahed.alali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "رجوى سلطان الظاهري",
                "personNumber": "30105",
                "phoneNumber": "1333421",
                "dateOfBirth": "1988-05-13",
                "emailAddress": "rajwa.aldhaheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "جاسم سعيد الزعابي",
                "personNumber": "20990",
                "phoneNumber": "4114101",
                "dateOfBirth": "1981-06-17",
                "emailAddress": "jasim.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد راشد الزعابي",
                "personNumber": "33990",
                "phoneNumber": "9655056",
                "dateOfBirth": "1984-07-31",
                "emailAddress": "saeed.alzaabi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمار يوسف الحمادي",
                "personNumber": "47590",
                "phoneNumber": "4433573",
                "dateOfBirth": "1980-01-16",
                "emailAddress": "a.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "نهله اسامه عركه",
                "personNumber": "47648",
                "phoneNumber": null,
                "dateOfBirth": "1979-11-16",
                "emailAddress": "nahla.arkeh@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "نورة عبيد الظاهري",
                "personNumber": "28527",
                "phoneNumber": "3141777",
                "dateOfBirth": "1983-08-16",
                "emailAddress": "noura.aldhahri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "ياسمين عبدالله الحوسني",
                "personNumber": "47581",
                "phoneNumber": "4408664",
                "dateOfBirth": "1991-02-07",
                "emailAddress": "yasmeen.alhosani@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "جواهر محمد الظاهري",
                "personNumber": "31652",
                "phoneNumber": "1122118",
                "dateOfBirth": "1982-12-03",
                "emailAddress": "jaldhaheri@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "حسن علي المرزوقي",
                "personNumber": "47638",
                "phoneNumber": null,
                "dateOfBirth": "1987-04-02",
                "emailAddress": "Hasan.almarzooqi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عائشة أحمد الهاملي",
                "personNumber": "27196",
                "phoneNumber": "6667338",
                "dateOfBirth": "1979-08-08",
                "emailAddress": "aisha.alhameli@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عبدالله محمد البريكي",
                "personNumber": "20708",
                "phoneNumber": "5812366",
                "dateOfBirth": "1980-09-18",
                "emailAddress": "abdullab@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان حسين الحمادي",
                "personNumber": "39577",
                "phoneNumber": "7666611",
                "dateOfBirth": "1985-09-26",
                "emailAddress": "sultan.alhammadi2@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 19
          },
          "300000001664482": {
            "list": [
              {
                "fullName": "ينال قاسم الخصاونة",
                "personNumber": "47627",
                "phoneNumber": "1882021",
                "dateOfBirth": "1979-10-02",
                "emailAddress": "yanal.alkhasoneh@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 1
          },
          "300000001664492": {
            "list": [
              {
                "fullName": "خالد حسن المرزوقي",
                "personNumber": "47501",
                "phoneNumber": "8081001",
                "dateOfBirth": "1987-05-27",
                "emailAddress": "k.almarzooqi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "لبنى علي السويدي",
                "personNumber": "21509",
                "phoneNumber": "4140144",
                "dateOfBirth": "1976-01-26",
                "emailAddress": "alsuwaidi@adcustoms.gov.ae",
                "gender": "Female"
              }
            ],
            "total": 2
          },
          "300000001664439": {
            "list": [],
            "total": 0
          },
          "300000001664547": {
            "list": [
              {
                "fullName": "فاطمة سعيد آل علي",
                "personNumber": "23587",
                "phoneNumber": "5101312",
                "dateOfBirth": "1976-07-13",
                "emailAddress": "fatma.aalali@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أحمد عمر بالحمر",
                "personNumber": "23467",
                "phoneNumber": "4727444",
                "dateOfBirth": "1980-02-06",
                "emailAddress": "ahmed.belhamer@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "منيره هزاع عبدالله حسين",
                "personNumber": "47616",
                "phoneNumber": null,
                "dateOfBirth": "2001-01-23",
                "emailAddress": "muneera.husain@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "جوليوس سانتياجو سالاريا",
                "personNumber": "30949",
                "phoneNumber": "6453865",
                "dateOfBirth": "1963-04-10",
                "emailAddress": "julius.salaria@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 4
          },
          "300000001664407": {
            "list": [
              {
                "fullName": "محمد سالم الزعابي",
                "personNumber": "20491",
                "phoneNumber": "6113449",
                "dateOfBirth": "1976-03-27",
                "emailAddress": "mzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حسن عبدالله الحمادي",
                "personNumber": "21020",
                "phoneNumber": "6138038",
                "dateOfBirth": "1982-07-01",
                "emailAddress": "hassan.hassan@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أسماء أحمد الفهيم",
                "personNumber": "47566",
                "phoneNumber": "6116101",
                "dateOfBirth": "1974-12-04",
                "emailAddress": "asma.alfahim@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "فهد أبوبكر النعمان",
                "personNumber": "23023",
                "phoneNumber": "6140344",
                "dateOfBirth": "1975-11-30",
                "emailAddress": "fahed.alnoaman@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 4
          },
          "300000001664447": {
            "list": [
              {
                "fullName": "حسن محمد الحمادي",
                "personNumber": "33583",
                "phoneNumber": "7817819",
                "dateOfBirth": "1992-06-16",
                "emailAddress": "hasan.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يوسف عبدالله المزيني",
                "personNumber": "33975",
                "phoneNumber": "3390932",
                "dateOfBirth": "1989-09-24",
                "emailAddress": "yousef.almezaini@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد مبارك الراشدي",
                "personNumber": "35049",
                "phoneNumber": "1988858",
                "dateOfBirth": "1987-01-05",
                "emailAddress": "mohamed.alrashdi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "نايف مبروك بن عمرو",
                "personNumber": "26919",
                "phoneNumber": "5333465",
                "dateOfBirth": "1989-06-17",
                "emailAddress": "nayef.binamro@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سالم الكثيري",
                "personNumber": "33978",
                "phoneNumber": "8884777",
                "dateOfBirth": "1993-01-01",
                "emailAddress": "mohamed.alkatheeri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد حيدر الحوسني",
                "personNumber": "34078",
                "phoneNumber": "7885517",
                "dateOfBirth": "1993-09-20",
                "emailAddress": "mohamed.alhosani4@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "جمال عبدالله الفلاسي",
                "personNumber": "34029",
                "phoneNumber": "1122044",
                "dateOfBirth": "1993-06-13",
                "emailAddress": "jamal.alfalasi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد غيث المهيري",
                "personNumber": "20608",
                "phoneNumber": "2320023",
                "dateOfBirth": "1979-01-05",
                "emailAddress": "saeed.almuhairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خليفة علي الحوسني",
                "personNumber": "23084",
                "phoneNumber": "6668010",
                "dateOfBirth": "1975-11-14",
                "emailAddress": "khalifa.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سيف سعيد الرميثي",
                "personNumber": "33982",
                "phoneNumber": "3233311",
                "dateOfBirth": "1989-04-30",
                "emailAddress": "saif.alromaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد سالم الراشدي",
                "personNumber": "23303",
                "phoneNumber": "4459505",
                "dateOfBirth": "1970-11-07",
                "emailAddress": "khaled.alrashedy@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عفاف حسن بني نور",
                "personNumber": "33622",
                "phoneNumber": "7099703",
                "dateOfBirth": "1974-09-21",
                "emailAddress": "afaf.baninoor@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "عبدالله باروت السويدي",
                "personNumber": "20820",
                "phoneNumber": "6220411",
                "dateOfBirth": "1979-12-25",
                "emailAddress": "abdulla.asuwaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مصبح رقيط الغفلي",
                "personNumber": "20760",
                "phoneNumber": "4644417",
                "dateOfBirth": "1982-05-22",
                "emailAddress": "Musabah2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فايع محمد القحطاني",
                "personNumber": "28472",
                "phoneNumber": "3032121",
                "dateOfBirth": "1989-05-01",
                "emailAddress": "fayea.faleh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعود شافى الهاجري",
                "personNumber": "33575",
                "phoneNumber": "5614111",
                "dateOfBirth": "1987-01-21",
                "emailAddress": "saoud.alhajiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عائشة جاسم الحمادي",
                "personNumber": "30853",
                "phoneNumber": "3856665",
                "dateOfBirth": "1987-11-10",
                "emailAddress": "aisha.alhamadi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "أحمد سعيد المهيري",
                "personNumber": "33987",
                "phoneNumber": "7666999",
                "dateOfBirth": "1990-10-06",
                "emailAddress": "ahmed.almehairi4@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فيصل عبيد الكتبي",
                "personNumber": "33620",
                "phoneNumber": "2055206",
                "dateOfBirth": "1990-04-29",
                "emailAddress": "fasal.obaid@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "موزة محمد المزروعي",
                "personNumber": "30826",
                "phoneNumber": "6213055",
                "dateOfBirth": "1974-02-18",
                "emailAddress": "moza.almazroui@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "مانع عبيد الحوسني",
                "personNumber": "34022",
                "phoneNumber": "3367700",
                "dateOfBirth": "1992-11-20",
                "emailAddress": "manea.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "هادي علي الأحبابي",
                "personNumber": "33588",
                "phoneNumber": "3077736",
                "dateOfBirth": "1988-08-26",
                "emailAddress": "hadi.alhbabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد سالم المري",
                "personNumber": "32948",
                "phoneNumber": "7677924",
                "dateOfBirth": "1992-11-10",
                "emailAddress": "hamad.almarri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعود عبدالرحمن الطنيجي",
                "personNumber": "20550",
                "phoneNumber": "7221828",
                "dateOfBirth": "1978-03-12",
                "emailAddress": "soud.saleh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "جاسم خليل الحوسني",
                "personNumber": "34037",
                "phoneNumber": "7419994",
                "dateOfBirth": "1993-11-10",
                "emailAddress": "jasem.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم خميس المحيربي",
                "personNumber": "26586",
                "phoneNumber": "6122210",
                "dateOfBirth": "1975-12-21",
                "emailAddress": "salem.almuhairbi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالرحمن حسن الزعابي",
                "personNumber": "20692",
                "phoneNumber": "6665938",
                "dateOfBirth": "1983-06-02",
                "emailAddress": "abdulrahman.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد بخيت الخيلي",
                "personNumber": "20429",
                "phoneNumber": "6426100",
                "dateOfBirth": "1971-06-05",
                "emailAddress": "hamad.alkhaili@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبيد حميد الزعابي",
                "personNumber": "20359",
                "phoneNumber": "6158030",
                "dateOfBirth": "1969-07-10",
                "emailAddress": "obaid.alzaabi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "صالح أبوبكر النعمان",
                "personNumber": "20395",
                "phoneNumber": "4199188",
                "dateOfBirth": "1971-03-20",
                "emailAddress": "Saleh_A@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي محمد الكثيري",
                "personNumber": "33585",
                "phoneNumber": "6666832",
                "dateOfBirth": "1993-01-09",
                "emailAddress": "ali.alkatheeri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عوض المنهالي",
                "personNumber": "43120",
                "phoneNumber": "5333337",
                "dateOfBirth": "1994-01-07",
                "emailAddress": "mohamed.almenhali3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي محمد الهاجري",
                "personNumber": "33974",
                "phoneNumber": "5422434",
                "dateOfBirth": "1993-09-29",
                "emailAddress": "ali.alhajeri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "جمعة عبدالله القبيسي",
                "personNumber": "23546",
                "phoneNumber": "5110073",
                "dateOfBirth": "1978-07-29",
                "emailAddress": "jumaa.alqubaisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "جاسم محمد الحمادي",
                "personNumber": "32192",
                "phoneNumber": "8996599",
                "dateOfBirth": "1990-12-04",
                "emailAddress": "jasem.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عمار محمد البريكي",
                "personNumber": "33599",
                "phoneNumber": "4182020",
                "dateOfBirth": "1991-06-29",
                "emailAddress": "ammar.albraiki@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد علي القبيسي",
                "personNumber": "34039",
                "phoneNumber": "1345932",
                "dateOfBirth": "1990-05-30",
                "emailAddress": "ahmed.alqubaisi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد أحمد المحرمي",
                "personNumber": "30253",
                "phoneNumber": "7777107",
                "dateOfBirth": "1990-06-04",
                "emailAddress": "mohamed.almuharrami@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "طلال علي بن حزيم",
                "personNumber": "30871",
                "phoneNumber": "6698976",
                "dateOfBirth": "1977-04-01",
                "emailAddress": "talal.obaid@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "تويم مبارك المنصوري",
                "personNumber": "33986",
                "phoneNumber": "3335003",
                "dateOfBirth": "1992-01-02",
                "emailAddress": "tuwaim.almansouri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "بطي محمد السويدي",
                "personNumber": "44943",
                "phoneNumber": "6999830",
                "dateOfBirth": "1977-03-31",
                "emailAddress": "buti.alsuwaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عبدالله المنهالي",
                "personNumber": "34004",
                "phoneNumber": "7611556",
                "dateOfBirth": "1992-07-08",
                "emailAddress": "mohamed.almenhali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ثاني عبدالله الرميثي",
                "personNumber": "43061",
                "phoneNumber": "5666097",
                "dateOfBirth": "1989-11-30",
                "emailAddress": "thani.alrumaithi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سيف الرميثي",
                "personNumber": "32969",
                "phoneNumber": "9161666",
                "dateOfBirth": "1987-10-07",
                "emailAddress": "mohamed.alromaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم حسين الخليفي",
                "personNumber": "20411",
                "phoneNumber": "1314440",
                "dateOfBirth": "1969-01-01",
                "emailAddress": "salemk2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله يوسف الحمادي",
                "personNumber": "20346",
                "phoneNumber": "8008385",
                "dateOfBirth": "1972-12-01",
                "emailAddress": "doc000865@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سالم المسلمي",
                "personNumber": "34013",
                "phoneNumber": "4161718",
                "dateOfBirth": "1987-07-14",
                "emailAddress": "mohamed.almusallami@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبيد إبراهيم الزعابي",
                "personNumber": "33607",
                "phoneNumber": "4688884",
                "dateOfBirth": "1993-08-07",
                "emailAddress": "obaid.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حامد محمد البلوكي",
                "personNumber": "23041",
                "phoneNumber": "4445599",
                "dateOfBirth": "1967-01-01",
                "emailAddress": "hamed.alblouki@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد جاسم الحوسني",
                "personNumber": "32945",
                "phoneNumber": "8816666",
                "dateOfBirth": "1993-10-17",
                "emailAddress": "ahmed.abdulla@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "غانم جمعة الرميثي",
                "personNumber": "20754",
                "phoneNumber": "7225389",
                "dateOfBirth": "1971-07-01",
                "emailAddress": "ghanem.alromaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد خلفان المهيري",
                "personNumber": "21505",
                "phoneNumber": "6132060",
                "dateOfBirth": "1968-08-30",
                "emailAddress": "malmuhairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فيصل علوان باعزب",
                "personNumber": "20551",
                "phoneNumber": "2666776",
                "dateOfBirth": "1972-07-30",
                "emailAddress": "faisal.ali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي أحمد المنصوري",
                "personNumber": "33980",
                "phoneNumber": "5222973",
                "dateOfBirth": "1989-08-16",
                "emailAddress": "ali.almansoori2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يوسف أحمد الشحي",
                "personNumber": "26973",
                "phoneNumber": "5520722",
                "dateOfBirth": "1987-10-03",
                "emailAddress": "yousef.alshehi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "وليد إبراهيم الحمادي",
                "personNumber": "20957",
                "phoneNumber": "7222995",
                "dateOfBirth": "1981-01-01",
                "emailAddress": "waleed3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم عبدالله الرميثي",
                "personNumber": "43063",
                "phoneNumber": "7777153",
                "dateOfBirth": "1991-10-25",
                "emailAddress": "salem.alrumaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "رهام خالد باحسين",
                "personNumber": "29184",
                "phoneNumber": "8008133",
                "dateOfBirth": "1979-05-07",
                "emailAddress": "reham.bahusein@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "ذياب محمد الزعابي",
                "personNumber": "20606",
                "phoneNumber": "5466645",
                "dateOfBirth": "1975-03-18",
                "emailAddress": "diyab.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سيف أحمد الحمادي",
                "personNumber": "26596",
                "phoneNumber": "8000374",
                "dateOfBirth": "1988-08-13",
                "emailAddress": "saif.mohamed@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عيسى سلطان الزعابي",
                "personNumber": "20607",
                "phoneNumber": "6900909",
                "dateOfBirth": "1973-09-25",
                "emailAddress": "eissa.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ماجد ناصر المحرمي",
                "personNumber": "33598",
                "phoneNumber": "9490902",
                "dateOfBirth": "1993-04-22",
                "emailAddress": "maged.almhrmy@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد سالم المري",
                "personNumber": "33011",
                "phoneNumber": "9249000",
                "dateOfBirth": "1992-08-11",
                "emailAddress": "saeed.almarri2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد بخيت الكتبي",
                "personNumber": "33610",
                "phoneNumber": "8900409",
                "dateOfBirth": "1989-05-23",
                "emailAddress": "hamad.alketbi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد صالح الحمادي",
                "personNumber": "46063",
                "phoneNumber": "2666169",
                "dateOfBirth": "1994-11-18",
                "emailAddress": "mohamad.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "إبراهيم عبدالله الطنيجي",
                "personNumber": "32938",
                "phoneNumber": "3633326",
                "dateOfBirth": "1992-05-19",
                "emailAddress": "ibrahim.altenaiji@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله هلال المهيري",
                "personNumber": "20988",
                "phoneNumber": "5000995",
                "dateOfBirth": "1986-07-02",
                "emailAddress": "abdulla.mehairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي قاسم صالح",
                "personNumber": "33606",
                "phoneNumber": "5555516",
                "dateOfBirth": "1992-05-06",
                "emailAddress": "ali.saleh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "يوسف محمد الحمادي",
                "personNumber": "34079",
                "phoneNumber": "4119977",
                "dateOfBirth": "1993-04-28",
                "emailAddress": "yousuf.alhammadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد محمد الزعابي",
                "personNumber": "34040",
                "phoneNumber": "3123129",
                "dateOfBirth": "1993-03-23",
                "emailAddress": "khalid.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر سالم البريكي",
                "personNumber": "33616",
                "phoneNumber": "7765200",
                "dateOfBirth": "1988-04-05",
                "emailAddress": "naser.albraiki@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "هزاع سلطان الحوسني",
                "personNumber": "33609",
                "phoneNumber": "4262020",
                "dateOfBirth": "1992-11-27",
                "emailAddress": "hazza.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سالم الحمادي",
                "personNumber": "43060",
                "phoneNumber": "6761000",
                "dateOfBirth": "1983-12-14",
                "emailAddress": "mohammad.alhammadi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد عبدالعزيز المهيري",
                "personNumber": "32947",
                "phoneNumber": "4469444",
                "dateOfBirth": "1989-09-04",
                "emailAddress": "saeed.almheiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد مبارك المزروعي",
                "personNumber": "22950",
                "phoneNumber": "4426404",
                "dateOfBirth": "1968-01-01",
                "emailAddress": "rashid.ali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد إبراهيم الحمادي",
                "personNumber": "33600",
                "phoneNumber": "4198991",
                "dateOfBirth": "1993-05-20",
                "emailAddress": "khalid.alhammadi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عيسى سالم المنصوري",
                "personNumber": "20542",
                "phoneNumber": "6676609",
                "dateOfBirth": "1977-07-01",
                "emailAddress": "eissa.bekheet@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خلفان سعيد الهاملي",
                "personNumber": "20507",
                "phoneNumber": "9533522",
                "dateOfBirth": "1974-02-27",
                "emailAddress": "khalfan.alhamly@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد سعيد الهاملي",
                "personNumber": "20360",
                "phoneNumber": "7115016",
                "dateOfBirth": "1971-05-27",
                "emailAddress": "ahmed.alhameli2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي سعيد الزعابي",
                "personNumber": "20743",
                "phoneNumber": "5117611",
                "dateOfBirth": "1978-05-29",
                "emailAddress": "Ali_R2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد حسن الحمادي",
                "personNumber": "45868",
                "phoneNumber": "6124248",
                "dateOfBirth": "1982-05-09",
                "emailAddress": "hamad.alhammadi3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر أحمد المهيري",
                "personNumber": "20736",
                "phoneNumber": "6102066",
                "dateOfBirth": "1977-02-03",
                "emailAddress": "Naser_A@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد عيسى زئي",
                "personNumber": "20415",
                "phoneNumber": "6184485",
                "dateOfBirth": "1969-01-01",
                "emailAddress": "mohamed.essa@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مطر علي الحوسني",
                "personNumber": "29642",
                "phoneNumber": "3541411",
                "dateOfBirth": "1984-04-26",
                "emailAddress": "matar.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد خلفان القبيسي",
                "personNumber": "32979",
                "phoneNumber": "4411199",
                "dateOfBirth": "1973-03-20",
                "emailAddress": "hamad.alqubaisi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد علي الكويتي",
                "personNumber": "20623",
                "phoneNumber": "6991688",
                "dateOfBirth": "1980-07-16",
                "emailAddress": "khaled.alkuwaiti@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مجدالدين عبداللطيف إدريس",
                "personNumber": "20356",
                "phoneNumber": "4955446",
                "dateOfBirth": "1964-07-01",
                "emailAddress": "majd.aldeen@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله سالم الزعابي",
                "personNumber": "20579",
                "phoneNumber": "8212188",
                "dateOfBirth": "1977-04-06",
                "emailAddress": "abdullaz3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد مصبح الرميثي",
                "personNumber": "39982",
                "phoneNumber": "3338600",
                "dateOfBirth": "1991-07-11",
                "emailAddress": "ahmed.alrumaithi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ياسر عبدالله الجنيبي",
                "personNumber": "20945",
                "phoneNumber": "6681128",
                "dateOfBirth": "1984-01-14",
                "emailAddress": "yjenaibi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سالم الكتبي",
                "personNumber": "33586",
                "phoneNumber": "6662259",
                "dateOfBirth": "1989-06-10",
                "emailAddress": "mohammad.alketbi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مبارك مرزوق الهاجري",
                "personNumber": "35043",
                "phoneNumber": "4347284",
                "dateOfBirth": "1990-02-28",
                "emailAddress": "mubarak.alhajeri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر طه الشروقي",
                "personNumber": "34030",
                "phoneNumber": "8880666",
                "dateOfBirth": "1987-12-25",
                "emailAddress": "naser.alshrooqi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلمان عبدالله الحمادي",
                "personNumber": "20462",
                "phoneNumber": "6426334",
                "dateOfBirth": "1973-12-23",
                "emailAddress": "salmanh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد حسن الحوسني",
                "personNumber": "33593",
                "phoneNumber": "3255303",
                "dateOfBirth": "1994-08-01",
                "emailAddress": "hamad.alhosani@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالعزيز يوسف السويدي",
                "personNumber": "29131",
                "phoneNumber": "4454590",
                "dateOfBirth": "1966-01-01",
                "emailAddress": "abdullaziz.alsuwaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله محمد الزعابي",
                "personNumber": "20725",
                "phoneNumber": "4111948",
                "dateOfBirth": "1981-12-29",
                "emailAddress": "Abdullah_m@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "بيات راشد الرميثي",
                "personNumber": "18293",
                "phoneNumber": "6627474",
                "dateOfBirth": "1972-07-29",
                "emailAddress": "bayat.alromaithi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "بخيت محمد الراشدي",
                "personNumber": "32943",
                "phoneNumber": "2010504",
                "dateOfBirth": "1990-05-08",
                "emailAddress": "bkheet.alrashedi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "فهد عدنان الزعابي",
                "personNumber": "34003",
                "phoneNumber": "4146640",
                "dateOfBirth": "1992-12-20",
                "emailAddress": "fahad.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد سالم المنهالي",
                "personNumber": "33604",
                "phoneNumber": "6005226",
                "dateOfBirth": "1992-10-03",
                "emailAddress": "mohammed.almnhaly@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد أحمد السومحي",
                "personNumber": "24311",
                "phoneNumber": "6791666",
                "dateOfBirth": "1985-07-11",
                "emailAddress": "mohammed.alsomhi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله ناصر الحميري",
                "personNumber": "43064",
                "phoneNumber": "7756565",
                "dateOfBirth": "1992-04-05",
                "emailAddress": "abdulla.alhmeiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مكتوم سعيد المنصوري",
                "personNumber": "23211",
                "phoneNumber": "6676992",
                "dateOfBirth": "1974-12-24",
                "emailAddress": "maktoom.almansouri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مبارك سعيد الكثيري",
                "personNumber": "20342",
                "phoneNumber": "6413397",
                "dateOfBirth": "1969-05-28",
                "emailAddress": "saeed.azzan@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد عبدالله المزروعي",
                "personNumber": "20028",
                "phoneNumber": "6149595",
                "dateOfBirth": "1973-01-16",
                "emailAddress": "Khalid@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد محمد المهري",
                "personNumber": "32191",
                "phoneNumber": "3388838",
                "dateOfBirth": "1991-02-09",
                "emailAddress": "hamad.almehri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله سعيد الرويشان",
                "personNumber": "22883",
                "phoneNumber": "9999080",
                "dateOfBirth": "1971-05-21",
                "emailAddress": "abdullah.saleh@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حسن علي الزعابي",
                "personNumber": "20723",
                "phoneNumber": "8719999",
                "dateOfBirth": "1979-06-27",
                "emailAddress": "hassan.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "همام سليم المحرمي",
                "personNumber": "33584",
                "phoneNumber": "6088882",
                "dateOfBirth": "1990-01-05",
                "emailAddress": "hammam.almuharrami@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محزم عبيد الزعابي",
                "personNumber": "34015",
                "phoneNumber": "7900949",
                "dateOfBirth": "1991-12-22",
                "emailAddress": "mohzem.alzaabi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد راشد الزعابي",
                "personNumber": "33589",
                "phoneNumber": "4444319",
                "dateOfBirth": "1993-01-23",
                "emailAddress": "mohammad.alzaabi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد محمد القطبه",
                "personNumber": "21222",
                "phoneNumber": "6413388",
                "dateOfBirth": "1973-01-01",
                "emailAddress": "khaled.alqutbah@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سلطان ناصر المحرمي",
                "personNumber": "34043",
                "phoneNumber": "8242074",
                "dateOfBirth": "1991-04-19",
                "emailAddress": "sultan.almuharrami@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خالد جاسم مكي",
                "personNumber": "21218",
                "phoneNumber": "5325322",
                "dateOfBirth": "1967-09-08",
                "emailAddress": "khmakki@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "ناصر عبدالله التميمي",
                "personNumber": "20401",
                "phoneNumber": "8008989",
                "dateOfBirth": "1970-06-25",
                "emailAddress": "n.altemimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعيد ناصر الحميري",
                "personNumber": "26592",
                "phoneNumber": "6957733",
                "dateOfBirth": "1977-11-09",
                "emailAddress": "saeed.alhemeiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد يوسف عبدالله",
                "personNumber": "20375",
                "phoneNumber": "5626026",
                "dateOfBirth": "1973-08-01",
                "emailAddress": "mohd.yousef@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "صالح علي المري",
                "personNumber": "24241",
                "phoneNumber": "8600720",
                "dateOfBirth": "1985-08-29",
                "emailAddress": "saleh.almerri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "زايد صالح الكثيري",
                "personNumber": "34012",
                "phoneNumber": "6655514",
                "dateOfBirth": "1991-12-03",
                "emailAddress": "zayed.alkathiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله فرج النعيمي",
                "personNumber": "33976",
                "phoneNumber": "6817181",
                "dateOfBirth": "1992-08-14",
                "emailAddress": "abdulla.alneaimi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "صادق علي الجنيدي",
                "personNumber": "24265",
                "phoneNumber": "1278923",
                "dateOfBirth": "1983-11-03",
                "emailAddress": "sadeq.aljunaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله عبدالحميد الحمادي",
                "personNumber": "32198",
                "phoneNumber": "6601611",
                "dateOfBirth": "1991-04-15",
                "emailAddress": "abdullah.sultan@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله أحمد المهيري",
                "personNumber": "32182",
                "phoneNumber": "4155050",
                "dateOfBirth": "1989-03-08",
                "emailAddress": "abdulla.almheiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عبدالله محمد المهيري",
                "personNumber": "20601",
                "phoneNumber": "8008820",
                "dateOfBirth": "1980-02-02",
                "emailAddress": "abdullam2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "علي سعيد المنصوري",
                "personNumber": "27223",
                "phoneNumber": "1222125",
                "dateOfBirth": "1983-08-15",
                "emailAddress": "ali.humaid@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سعد جمعة السويدي",
                "personNumber": "27927",
                "phoneNumber": "7227788",
                "dateOfBirth": "1975-07-09",
                "emailAddress": "saad.alsuwaidi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "أحمد علي العوضي",
                "personNumber": "20975",
                "phoneNumber": "9375554",
                "dateOfBirth": "1984-04-03",
                "emailAddress": "aawadi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حنان سعيد المهيري",
                "personNumber": "32910",
                "phoneNumber": "6667006",
                "dateOfBirth": "1977-11-10",
                "emailAddress": "hanan.almuhairi@adcustoms.gov.ae",
                "gender": "Female"
              },
              {
                "fullName": "إبراهيم محمد النويس",
                "personNumber": "20403",
                "phoneNumber": "6416406",
                "dateOfBirth": "1972-01-20",
                "emailAddress": "ebrahim.alnuwais@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خليفة أحمد المهيري",
                "personNumber": "20397",
                "phoneNumber": "8008405",
                "dateOfBirth": "1973-02-14",
                "emailAddress": "khalifa.almuhairi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "راشد سعيد المري",
                "personNumber": "45915",
                "phoneNumber": "9086060",
                "dateOfBirth": "1987-11-14",
                "emailAddress": "rashed.almarri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "خليفة أحمد المهيري",
                "personNumber": "26929",
                "phoneNumber": "6111229",
                "dateOfBirth": "1974-07-18",
                "emailAddress": "khalifa.almehairi2@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "حمد عبدالله الجمالي",
                "personNumber": "17959",
                "phoneNumber": "5404450",
                "dateOfBirth": "1970-04-28",
                "emailAddress": "hamad.jamali@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "مبارك سبيل الشامسي",
                "personNumber": "34546",
                "phoneNumber": "7829292",
                "dateOfBirth": "1972-07-30",
                "emailAddress": "mubarak.alshamisi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "بدر خالد المهيري",
                "personNumber": "31832",
                "phoneNumber": "5461232",
                "dateOfBirth": "1986-03-18",
                "emailAddress": "bader.almehairi@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "سالم عبدالله الحميري",
                "personNumber": "18107",
                "phoneNumber": "6111098",
                "dateOfBirth": "1971-01-02",
                "emailAddress": "salem.alhemeiri@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "محمد خلفان المنصوري",
                "personNumber": "31829",
                "phoneNumber": "6666192",
                "dateOfBirth": "1987-05-26",
                "emailAddress": "mohamed.almansouri3@adcustoms.gov.ae",
                "gender": "Male"
              },
              {
                "fullName": "عوض عمر بلبحيث",
                "personNumber": "30958",
                "phoneNumber": "3434314",
                "dateOfBirth": "1970-08-28",
                "emailAddress": "awadh.omar@adcustoms.gov.ae",
                "gender": "Male"
              }
            ],
            "total": 139
          },
          "300000038999754": {
            "list": [],
            "total": 0
          },
          "300000038999792": {
            "list": [],
            "total": 0
          },
          "300000038999673": {
            "list": [],
            "total": 0
          },
          "300000038999960": {
            "list": [],
            "total": 0
          },
          "300000038999573": {
            "list": [],
            "total": 0
          },
          "300000043303231": {
            "list": [],
            "total": 0
          },
          "300000043303066": {
            "list": [],
            "total": 0
          },
          "300000043303124": {
            "list": [],
            "total": 0
          },
          "300000038999740": {
            "list": [],
            "total": 0
          },
          "300000038999702": {
            "list": [],
            "total": 0
          },
          "300000038999794": {
            "list": [],
            "total": 0
          },
          "300000043303285": {
            "list": [],
            "total": 0
          },
          "300000038999900": {
            "list": [],
            "total": 0
          },
          "300000038999901": {
            "list": [],
            "total": 0
          },
          "300000038999629": {
            "list": [],
            "total": 0
          },
          "300000043303139": {
            "list": [],
            "total": 0
          },
          "300000038999647": {
            "list": [],
            "total": 0
          },
          "300000043303073": {
            "list": [],
            "total": 0
          },
          "300000043303078": {
            "list": [],
            "total": 0
          },
          "300000043303289": {
            "list": [],
            "total": 0
          },
          "300000043303154": {
            "list": [],
            "total": 0
          },
          "300000043303332": {
            "list": [],
            "total": 0
          },
          "300000043303200": {
            "list": [],
            "total": 0
          },
          "300000043303300": {
            "list": [],
            "total": 0
          },
          "300000038999723": {
            "list": [],
            "total": 0
          },
          "300000043303405": {
            "list": [],
            "total": 0
          },
          "300000038999727": {
            "list": [],
            "total": 0
          },
          "300000043303051": {
            "list": [],
            "total": 0
          },
          "300000043303046": {
            "list": [],
            "total": 0
          },
          "300000043303218": {
            "list": [],
            "total": 0
          },
          "300000043303317": {
            "list": [],
            "total": 0
          },
          "300000043303013": {
            "list": [],
            "total": 0
          },
          "300000038999761": {
            "list": [],
            "total": 0
          },
          "300000043303319": {
            "list": [],
            "total": 0
          },
          "300000043303214": {
            "list": [],
            "total": 0
          },
          "300000043303331": {
            "list": [],
            "total": 0
          },
          "300000038999555": {
            "list": [],
            "total": 0
          },
          "300000043303072": {
            "list": [],
            "total": 0
          },
          "300000043303026": {
            "list": [],
            "total": 0
          },
          "300000038999812": {
            "list": [],
            "total": 0
          },
          "300000043303201": {
            "list": [],
            "total": 0
          },
          "300000043303050": {
            "list": [],
            "total": 0
          },
          "300000043303106": {
            "list": [],
            "total": 0
          },
          "300000043303152": {
            "list": [],
            "total": 0
          },
          "300000043303107": {
            "list": [],
            "total": 0
          }
        }
        return res['depID']
      // return new Promise(function (resolve, reject) {
      //     $.ajax({
      //         url: URL,
      //         type: "GET",
      //         headers: {
      //             Authorization: "Bearer " + Liferay.authToken,
      //             'ApiKey': 'TUlJQ1dnSUJBQUtCZ0hjRzVMWkdXSXpRUnhwdW54VDE5NmozZTkzL0I5eTZETTB3ZlBaejhyTDFZek5VZFNIUgpVclZ6TFBZUDUydTI0cU9VSUhyU2dZRXVlWEFwcTBQK2VTT1pMdk5xZ3V3SVR0Yk9OQTNLU0srOWxWaFpyQy9BClNmU2dON3psRk54UFJDMGdwaFV4QUJZdEFpdlQzR2xQMWZDK1BheFpGM2prTFFUbm5YTW9jREk3QWdNQkFBRUMKZ1lCcUt4cnN2eGlUR2VDaVloUEI1Wmd2L2ZoZHp2TGJYcFMybmM2SkltbFVXVzlQeE1EcUZrVlpGay8vZDdZcgpyU2pCVWdvYXBCUGgvMnRRc2NwVFR2UUxvYW8vTHE5NTNSdEx6dlViSVFoUjkrWWpIaml2aVM5THZSK3JjSUhyCk5Zdzc1YnlGM3lNMGpvK1dSbXpBVlpJd1AwVWVhcEpZTWN2S1FraElGYWNyMlFKQkFNK1Y5T3pFR2VBN3BTMXUKVFR6UVJkSEQwQjNUMkRSdFl0OHI4RVFTamErNEp4T0VNTGpMV1dycUkxWi9mOThpK3JUN3B1d0lRNUxMVm92ZgpkK0dzWTFVQ1FRQ1N5WGsyVnBaTVFxVFF0NS9MZ1dET2tacHV1WGt6cEQ5bmRReDB6cXIxWGF6NkNvNlVnQS82CmtxckV2YWJCWXBqNmlya2VTQTF0SlhUMmptcWYzVjlQQWtBL1I0MDBKOHRqaVlzZXdFTVhTTDRmNWJzcGZJeXAKM3JhSEpaUEdqSWxZaWFDUDJIb3B1d04xRGc3YnJWNURuUndqMDVyYzFPQVVmWnZTWTdyZHRubEpBa0FQZVhmeQoxNHYrdkNQZDhRM0NpWUFvSnNkdUZ0V0ZNVEtSK0kvNG5IVC9hd0c2Vm5TVGlUQ21ET0k5M1hSLy9LSDkvN1BtClVsaEFBbXZqTmo1ZFhod1hBa0JOYnp4OVhDY1RoTlpkNUNnV1NrY09FRVEwL3FxcUJiSW5NRmZOL2l1eEVaUUEKUjVvRVdOTWtSeTRzeHY0dXVOOFg0YnRQeUw5WHdvcEJIMGtZQTEvNQ==',
      //             languageId: Liferay.ThemeDisplay.getLanguageId(),

      //         },
      //         success: function (res) {
      //             resolve(res); // Resolve the Promise with the successful response data
      //         },
      //         error: function (error) {
      //             reject(error); // Reject the Promise with the error object
      //         },
      //     });
      // });
  }

  async function getEmployees(sectionName, sectionID) {
      $("#emp-list").html("");
      $(".empView").addClass("d-none");
      $(".loader-container").removeClass("d-none");
      $(".loader-container").addClass("d-flex");

      tempEmpArray = await getDepartmentEmployees(sectionID, pageNum);
      sectionId = sectionID;
      lastPage = Math.ceil(tempEmpArray?.total / pageSize);
      selectedSection = sectionName;
      $("#pageNum").html(pageNum + "/" + lastPage);

      for (let emp of tempEmpArray.list) {
          let empItem = `<div class="col-xl-3 mb-4 col-sm-6">
<div class="emp--grid">
<div class="emp-profile--image"></div>
<p>${emp?.fullName}</p>
<ul class="contact--controls">
<li class="contact-email-btn">
<a href="mailto:${emp?.emailAddress}" 
class="m-auto">
<i class="icon-envelope-alt"></i></a>
</li>
<li class="contact-phone-btn">
<a href="tel:${emp?.phoneNumber}" 
class="m-auto">
<i class="icon-phone"></i></a>
</li>
</ul>
</div>
</div>`;

          $("#emp-list").append(empItem);
      }

      $(".loader-container").addClass("d-none");
      $(".loader-container").removeClass("d-flex");
      $(".empView").removeClass("d-none");
  }

  $(".backOrgChart").on("click", function () {
      pageNum = 1;
      $(".empView").addClass("d-none");
      $(".chart-container").removeClass("d-none");
  });
});