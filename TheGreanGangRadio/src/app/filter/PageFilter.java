package app.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.log4j.Logger;

import app.util.LogUtils;

@WebFilter(filterName = "PageFilter", urlPatterns = "/faces/indexDJ.xhtml")
public class PageFilter implements Filter {
	private static final Logger log =  LogUtils.getLogger(PageFilter.class);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.debug("forwarding to PageSelector again");
		request.getServletContext().getRequestDispatcher("/faces/index.xhtml").forward(request, response);
		
//		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
