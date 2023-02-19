package java_concurrency_in_practice._05_basicbuildingblocks;

import net.jcip.annotations.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

@ThreadSafe
public class Factorizer implements Servlet {
    private final Computable<BigInteger, BigInteger[]> c = new Computable<BigInteger, BigInteger[]>() {
        @Override
        public BigInteger[] compute(BigInteger arg) throws InterruptedException {
            return factor(arg);
        }
    };
    private final Computable<BigInteger, BigInteger[]> cache = new Memoizer<>(c);

    private BigInteger[] factor(BigInteger arg) {
        return new BigInteger[0];
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        try{
            BigInteger i = extracFromRequest(req);
            encodeInfoResponse(resp, cache.compute(i));
        } catch (InterruptedException e) {
            encodeError(resp, "factorization interrupted");
        }
    }

    private void encodeError(ServletResponse resp, String factorization_interrupted) {
    }

    private void encodeInfoResponse(ServletResponse resp, BigInteger[] compute) {
    }

    private BigInteger extracFromRequest(ServletRequest req) {
        return null;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
