package com.iaap.controller.education.Master;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iaap.comman.controller.Comman_controller;
import com.iaap.dao.education.Master.CountryDAO;
import com.iaap.model.education.Master.TB_COUNTRY_MSTR;

@Controller
public class CountryController {
	
	@Autowired
	private SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sf) {
		this.sessionFactory = sf;
	}
	
	@Autowired
	private CountryDAO Cdao;
	Comman_controller common = new Comman_controller();
	
	
	@GetMapping(value = "/country_url")
	public ModelAndView country_url(ModelMap model,HttpSession session,
			 @RequestParam(value = "msg", required = false) String msg ) {
		
	        model.addAttribute("msg", msg);
	      //  model.put("getEducationStatusList", common.getEducationStatusList());
	     //	model.put("list", ddao.getAllData(""));//fetch to table
		return new ModelAndView("Country_Tiles");
	}
	
	
	///save action 
	@PostMapping(value = "/Country_action" )
	public ModelAndView Country_action(@Validated @ModelAttribute("Country_cmd") TB_COUNTRY_MSTR td,BindingResult result,
			HttpServletRequest request, ModelMap model, HttpSession session,Principal principal,
            RedirectAttributes ra) {
		
//		String roleid = session.getAttribute("roleid").toString();
//		  Boolean val = roledao.ScreenRedirect("", roleid); 
//		  if (val ==false) 
//		  { return new ModelAndView("redirect:403"); } 
	
			int id = td.getId()> 0 ? td.getId() : 0;				
			Date date = new Date();
			String username = principal.getName();
			System.out.println(username);
			
			String country = request.getParameter("country");
			
			if (country == null || country.trim().equals("")) {
				model.put("msg", " Please Enter Country");
				return new ModelAndView("redirect:country_url");
			}
			
			Session sessionHQL =this.sessionFactory.openSession();
			Transaction	tx = sessionHQL.beginTransaction();
		
			
			try{
			
				Long c = (Long) sessionHQL.createQuery("select count(id) from  TB_COUNTRY_MSTR where upper(country)=:country and  upper(status)=:status and id !=:id")
						.setParameter("country", td.getCountry().toUpperCase())
						.setParameter("status", td.getStatus().toUpperCase())
						.setParameter("id", id).uniqueResult();
				if (id == 0) {
					td.setCreated_by(username);
					td.setCreated_date(date);
					if (c == 0) {
						sessionHQL.save(td);
						sessionHQL.flush();
						sessionHQL.clear();
                     
						 ra.addAttribute("msg", "Data Saved Successfully.");

					} else {
						 ra.addAttribute("msg", "Data already Exist.");
					}
				}
//				else {
//					td.setModified_by(username);
//					td.setModified_date(date);
//					if (c == 0) {
//						
//						String msg = ddao.UpdateName(td);
//						 ra.addAttribute("msg", msg);
//					} else {
//						 ra.addAttribute("msg", "Data already Exist.");
//					}
//				}
				tx.commit();
				
			}catch(RuntimeException e){
				try{
				
					ra.addAttribute("msg", "roll back transaction");
				}catch(RuntimeException rbe){
					ra.addAttribute("msg", "Couldn�t roll back transaction " + rbe);
				}
				throw e;
			}finally{
				if(sessionHQL!=null){
				   sessionHQL.close();
				}
				
			}	
		
			return new ModelAndView("redirect:country_url");
		}
	
	
	//search data table
	@PostMapping("/getCountry_data")
	public @ResponseBody List<Map<String, Object>> getCountry_data(int startPage, int pageLength,
			String Search, String orderColunm, String orderType, String country,
			HttpSession sessionUserId) {
		return Cdao.DataTablegetCountryDataList(startPage, pageLength, Search, orderColunm, orderType, country);

	}

	@PostMapping("/getCountry_dataCount")
	public @ResponseBody long getTotalRegistration_dataCount(HttpSession sessionUserId, String Search, String country) {

		return  Cdao.DataTableCountryDataTotalCount(Search, country);

	}
	
	
	@RequestMapping(value = "/Edit_Country")
    public ModelAndView Edit_Country(@ModelAttribute("id1") String updateid,ModelMap Mmap,
                    @RequestParam(value = "msg", required = false) String msg, Authentication authentication,
                    HttpSession sessionEdit) {
            
		TB_COUNTRY_MSTR Country_Details = Cdao.getCountryByid(Integer.parseInt(updateid));
//                    Mmap.put("Edit_StateCMD", stateDetails);
//                    Mmap.put("country_id", mcommon.getMedCountryName("", sessionEdit));
//                    Mmap.put("getStatusMasterList", mcommon.getStatusMasterList());
                    Mmap.put("msg", msg);
                    Mmap.put("Country_Details", Country_Details);
            return new ModelAndView("edit_Country_Tiles");
    }
	
			             
							@RequestMapping(value = "/Edit_Country_action", method = RequestMethod.POST)
			                public ModelAndView Edit_Country_action(@ModelAttribute("Edit_Country_cmd") TB_COUNTRY_MSTR rs,
			                                HttpServletRequest request,ModelMap model, HttpSession session,@RequestParam(value = "msg", required = false) String msg,RedirectAttributes ra) throws ParseException {
			                
			                        String username = session.getAttribute("username").toString();

			                        int id = Integer.parseInt(request.getParameter("id"));
			                        String country = request.getParameter("country").trim();
			                        String status = request.getParameter("status");
			                         
			                        System.err.println("country---"+country+"---"+status);
			                        
			                                
			                        Session session1 = this.sessionFactory.openSession();
			  	                    Transaction tx = session1.beginTransaction();
			                                 try {
			                                         Query q0 = session1.createQuery("select count(id) from TB_COUNTRY_MSTR where country=:country and status=:status and id !=:id");
			                                                q0.setParameter("country",country);
			                                                q0.setParameter("status", status); 
			                                                q0.setParameter("id",  id); 
			                                                Long c = (Long) q0.uniqueResult();
			                                                
			                                                if(c==0) {
			                                                         String hql = "update TB_COUNTRY_MSTR set country=:country,status=:status,modified_by=:modified_by,modified_date=:modified_date"
			                                                                                + " where id=:id";
			                                                                           
			                                                      Query query = session1.createQuery(hql)
			                                                                              .setParameter("country",country)
			                                                                              .setParameter("status",status)
			                                                                              .setParameter("modified_by", username)
			                                                                              .setParameter("modified_date", new Date())
			                                                                              .setParameter("id",id);
			                                                            msg = query.executeUpdate() > 0 ? "1" :"0";
			                                                            tx.commit(); 
			                                                            
			                                                            if(msg == "1") {
			                                                                     ra.addAttribute("msg", "Data Updated Successfully.");
			                                                            }
			                                                            else {
			                                                            	ra.addAttribute("msg", "Data Not Updated.");
			                                                            }
			                                                }
			                                                else {
			                                                	ra.addAttribute("msg", "Data already Exist.");
			                                                }
			                                        
			                                  }catch(RuntimeException e){
			                              try{
			                                      tx.rollback();
			                                      model.put("msg", "roll back transaction");
			                              }catch(RuntimeException rbe){
			                                      model.put("msg", "Couldnï¿½t roll back transaction " + rbe);
			                              }
			                              throw e;
			                             
			                                }finally{
			                                        if(session1!=null){
			                                                session1.close();
			                                        }
			                                }
			                        return new ModelAndView("redirect:country_url");
			                }



	
				///delete action
				@PostMapping(value = "/deleteUrl")
				@ResponseBody  ModelAndView deleteUrl(@ModelAttribute("idd") int id,BindingResult result,RedirectAttributes ra, 
	            HttpServletRequest request, ModelMap model, HttpSession session1) {

					List<String> liststr = new ArrayList<String>();
							
								Session session =this.sessionFactory.openSession();
								Transaction tx = session.beginTransaction();
								
								String username = session1.getAttribute("username").toString();
//								try {
							System.err.println("id----------"+id);
									int app = session.createQuery("update TB_COUNTRY_MSTR set modified_by=:modified_by,modified_date=:modified_date,status=:status where id=:id")
											.setParameter("id", id)
											.setParameter("modified_by", username)
											.setParameter("modified_date", new Date())
											.setParameter("status", "2")
											.executeUpdate();
									tx.commit();
									session.close();
									if (app > 0) {
										liststr.add("Data Deleted Successfully.");
									} else {
										liststr.add("Data not Deleted.");
									}
									ra.addAttribute("msg",liststr.get(0));
//								} catch (Exception e) {
//									liststr.add("CAN NOT DELETE THIS DATA BECAUSE IT IS ALREADY IN USED.");
//									ra.addAttribute("msg",liststr.get(0));
//								}
								return new ModelAndView("redirect:country_url");
							}
}
