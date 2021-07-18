package basic;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 无状态的Servlet
 */
@ThreadSafe
public class StateLessFactorizer implements Servlet {
    public void init(ServletConfig servletConfig) throws ServletException {
        
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        BigInteger i = extratFromRequest(servletRequest);
        BigInteger[] factors = factor(i);
        encodeIntoResponse(servletResponse, factors);
    }

    private void encodeIntoResponse(ServletResponse servletResponse, BigInteger[] factors) {
    }

    /**
     * 因数分解
     * @param i
     * @return
     */
    private BigInteger[] factor(BigInteger i) {
        return new BigInteger[0];
    }

    private BigInteger extratFromRequest(ServletRequest servletRequest) {
        return null;
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {

    }
}
