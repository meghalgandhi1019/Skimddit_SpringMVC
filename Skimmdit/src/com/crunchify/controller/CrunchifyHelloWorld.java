package com.crunchify.controller;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
 
 
@Controller
public class CrunchifyHelloWorld {
	
	final static Map<String,String>userdatabase = new HashMap<>();
	final static Map<String,LinkDatabase> links = new HashMap<>();
	final static Map<String,ArrayList<String>> voteusermap = new HashMap<>();
	final static Map<String,ArrayList<String>> voteusermap_dislike = new HashMap<>();
	
		
	@RequestMapping(value="/login", method=RequestMethod.GET )
	public ModelAndView loginget(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if(session.getAttribute("username")!=null)
		{
			
			return new ModelAndView("submit");
		}
		return new ModelAndView("login");
		
	}
	
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request,UserLoginForm user)
	{
		
		
		
		HttpSession session = request.getSession();
		if(session.getAttribute("username")!=null)
		{
		
			return new ModelAndView("submit");
				
				
	    }
		
	
		
			
			if((!user.getpasscode().equals(user.getcpasscode())|| (user.getusername().isEmpty()) || (user.getpasscode().isEmpty()) || userdatabase.containsKey(user.getusername())))
			{
				
				
				request.setAttribute("Signin", "failed");
				return new ModelAndView("login");
				
				
			}
			
			
				userdatabase.put(user.getusername(), user.getpasscode());
			    System.out.println("CrunchifyHelloWorld.login()");
				session.setAttribute("username", user.getusername());
				return new ModelAndView("submit");
				
		
		}
	
	@RequestMapping(value="/signin", method = RequestMethod.POST)
	public ModelAndView signin(HttpServletRequest request, @RequestParam("username") String username, 
														   @RequestParam("passcode") String password)
	{
		int flag=0;
		
		HttpSession session = request.getSession();
		if(session.getAttribute("username")!=null)
		{
			
			return new ModelAndView("submit");
		}
		
		
		
		if(username.isEmpty()|| password.isEmpty() || !userdatabase.containsKey(username) || !password.equals(userdatabase.get(username)))
		{
			flag=1;
			
			request.setAttribute("loginfailed", "yes");
			return new ModelAndView("signin");
		}
		
			session.setAttribute("username", username);
			return new ModelAndView("submit");
		
		
	}
	
	@RequestMapping(value="/signin", method = RequestMethod.GET)
	public ModelAndView getlogin(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		if(session.getAttribute("username")!=null)
		{
			
			return new ModelAndView("submit");
		}
		return new ModelAndView("signin");
	}
	
		
	@RequestMapping("/logout")
	public ModelAndView logout(HttpServletRequest request)
	{
		request.getSession().removeAttribute("username");
		
		request.getSession().invalidate();
	
		return new ModelAndView("login");
	}
	
	
	
	@RequestMapping(value="/link_handler", method = RequestMethod.POST)
	public ModelAndView addlink(HttpServletRequest request, @RequestParam("linkname") String linkname,
			@RequestParam("url") String URL)
	{
		int flag=0;
		
		if(linkname.isEmpty() || URL.isEmpty())
		{
			flag=1;
			request.setAttribute("failure", "true");

			return new ModelAndView("submit");
		}
		
			
			LinkDatabase newlink = new LinkDatabase();
			String username= (String) request.getSession().getAttribute("username");
			newlink.setname(linkname);
			newlink.setURL(URL);
			newlink.setvote(0);
			newlink.setusername(username);
			
			links.put(linkname,newlink);
		
			request.setAttribute("success", "true");

			return new ModelAndView("submit");
			
	
	}
	
	@RequestMapping(value="/load_dashboard", method = RequestMethod.GET)
	public ModelAndView load_dashboard(HttpServletRequest request)
	{
		
		System.out.println("in dashboard");
		
		int[] vote = new int[links.size()];
        int i=0;
		for(String s : links.keySet())
   		{	
		  vote[i] = links.get(s).getvote();
		  i++;
		
	    }
		
	Arrays.sort(vote);
	String[] sortedlinks = new String[vote.length];
	
	i=0;
	int j=0;
	
	for(int k=0;k<vote.length;k++)
	{
		for(String s : links.keySet())
   		{	
			
			if(links.get(s).getvote()==vote[i])
				
			{
				int flag=0;
				for(int l=0; l<k;l++)
				{
					if(sortedlinks[l].equals(links.get(s).getname()))
					{
						flag=1;
						break;
					}
				}
				if(flag==0)
				{
					sortedlinks[j]=links.get(s).getname();
					i++;
					j++;
				    break;
				}
			}
		
        }
	}	
	
	for(int d=0;d<sortedlinks.length;d++)
	{
		System.out.println("---------"+sortedlinks[d]);
	}
	
    System.out.println("-------------------------------------------------------------------------------");
	HttpSession session = request.getSession();
	
	session.setAttribute("links", links);
	if(sortedlinks.length!=0)
	{
		session.setAttribute("sortedlinks",sortedlinks);
	}
	else
	{
		
		session.setAttribute("empty","true");
	}
	
	return new ModelAndView("show_dashboard");
	
}
	
	@RequestMapping(value="/vote_handler", method = RequestMethod.GET)
	public ModelAndView vote_handler(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		if(request.getSession().getAttribute("username")==null)
        {
            request.setAttribute("login","false");
            //response.sendRedirect("login");
            return new ModelAndView("login");
        }
            
            
        int flag=0;
        int insertflag=0;
         String username= (String)request.getSession().getAttribute("username");
                 
         
		
                 
			String link_name="";
			
                    
			 Enumeration e = request.getParameterNames();
			    while (e.hasMoreElements()) {
			      String name = (String) e.nextElement();
                          
			      String values[] = request.getParameterValues(name);
                         
			      if (values != null) {
			        for (int i = 0; i < values.length; i++) {
			          link_name=values[i];
			        }
			      }
			    }
			    System.out.print("link name username" + link_name + username);
			    LinkDatabase link = links.get(link_name);
                         ArrayList userlist=null;
                        for(String s : voteusermap.keySet())
                         {	
                             
                             if(s.equals(link_name))
                             {
                                 userlist = voteusermap.get(s);
                                 break;
                             }
                                 
                         }
                        if(userlist!=null)
                        {
                            
                            insertflag=1;
                            System.out.print("userlist"+userlist);
                            Iterator i = userlist.iterator();
                             while(i.hasNext())
                             {
                                  
                                 if(i.next().equals(username))
                                 {
                                	 
                                     flag=1;
                                     break;
                                 }
                             }
                        } 
                        if(flag==1)
                        {
                        	
                        	 return new ModelAndView("redirect:/load_dashboard");
                        }
                         else
                         {
                             
                             
                             if(insertflag==0)
                             {
                                 ArrayList newuserlist = new ArrayList();
                                 newuserlist.add(username);     
                                 voteusermap.put(link_name, newuserlist);
                             }
                             else
                             {
                                userlist.add(username);
                                 voteusermap.put(link_name, userlist);
                             }
                              
                              int newvote = link.getvote();
                        
                              newvote++;
                              link.setvote(newvote);
		                 for(String s : voteusermap_dislike.keySet())
                              {	
                             
                                 if(s.equals(link_name))
                                 {
                                     userlist = voteusermap_dislike.get(s);
                                     userlist.remove(username);
                                     break;
                                 }
                                 
                             }
                              links.put(link_name, link);
                             }
                       
                        return new ModelAndView("redirect:/load_dashboard");
	
		
	}

	@RequestMapping(value="/dislike_handler", method = RequestMethod.GET)
	public ModelAndView dislike_handler(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		if(request.getSession().getAttribute("username")==null)
        {
            request.setAttribute("login","false");
           
            
            return new ModelAndView("login");
        }
          int flag=0;
          int insertflag=0;
         String username= (String)request.getSession().getAttribute("username");
         
		if(request.getSession().getAttribute("username")!=null)
		{
			String link_name="";
			
			 Enumeration e = request.getParameterNames();
			    while (e.hasMoreElements()) {
			      String name = (String) e.nextElement();
			      String values[] = request.getParameterValues(name);
			      if (values != null) {
			        for (int i = 0; i < values.length; i++) {
			          link_name=values[i];
			        }
			      }
			    }
			    
			     LinkDatabase link = links.get(link_name);
                         ArrayList userlist=null;
                         
                         for(String s : voteusermap_dislike.keySet())
                         {	
                             
                             if(s.equals(link_name))
                             {
                                 userlist = voteusermap_dislike.get(s);
                                 break;
                             }
                                 
                         }
                        if(userlist!=null)
                        {
                            
                            insertflag=1;
                            
                            Iterator i = userlist.iterator();
                             while(i.hasNext())
                             {
                                  
                                 if(i.next().equals(username))
                                 {
                                     flag=1;
                                     break;
                                 }
                             }
                        } 
                        if(flag==1)
                        {
                        	 return new ModelAndView("redirect:/load_dashboard");
                            
                        }
                         else
                         {
                             

                             if(insertflag==0)
                             {
                                 ArrayList newuserlist = new ArrayList();
                                 newuserlist.add(username);     
                                 voteusermap_dislike.put(link_name, newuserlist);
                             }
                             else
                             {
                                userlist.add(username);
                                 voteusermap_dislike.put(link_name, userlist);
                             }
                              
                              int newvote = link.getvote();
                        
                              newvote--;
                              if(newvote<=0)
                              {
                                  newvote=0;
                              }
                              link.setvote(newvote);
                              for(String s : voteusermap.keySet())
                              {	
                             
                             if(s.equals(link_name))
                             {
                                 userlist = voteusermap.get(s);
                                 userlist.remove(username);
                                 break;
                             }
                                 
                         }
		
                              links.put(link_name, link);
                              return new ModelAndView("redirect:/load_dashboard");
                              }
                         
                         
	
	}
    
	
		return new ModelAndView("show_dashboard");
	}
	
		
	}
