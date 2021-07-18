package basic;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

/**
 * 缓存对其最近计算结果Servlet
 */
@ThreadSafe
public class SynchronizedFactorizer implements Servlet {
    @GuardedBy("this") private BigInteger lastNumber;
    @GuardedBy("this") private BigInteger[] lastFactors;

    public void init(ServletConfig servletConfig) throws ServletException {
        
    }

    public ServletConfig getServletConfig() {
        return null;
    }

    /**
     * 效率低下
     * @param servletRequest
     * @param servletResponse
     * @throws ServletException
     * @throws IOException
     */
    public synchronized void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        BigInteger i = extratFromRequest(servletRequest);
        if(i.equals(lastNumber))
            encodeIntoResponse(servletResponse, lastFactors);
        else {
            BigInteger[] factors = factor(i);
            lastNumber = i;
            lastFactors = factors;
            encodeIntoResponse(servletResponse, factors);
        }
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
