#set($domain=$!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))
package $!{packageName};

import sc.commons.base.api.BaseResponse;
import sc.fx.apibase.annotation.hessian.HessianDispatcherServlet;
import com.fg.commons.hessian.header.HeaderInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.annotation.WebServlet;

/**
 * <一句话功能简述> <功能详细描述>
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

@WebServlet(value = "/$!{domain}Service")
@RestController(value = "$!{domain}Service")
@RequestMapping(value = "$!{domainUrl}")
public class $!{domainName}ServiceImpl extends HessianDispatcherServlet implements I$!{domainName}Service{
private static final Logger logger=LoggerFactory.getLogger($!{domainName}ServiceImpl.class);


@RequestMapping(value = "/$!{domain}", method = RequestMethod.POST, produces = {"application/json"})
@Override
public $!{domainName}Response call(HeaderInfo t,@RequestBody $!{domainName}Request request){
        $!{domainName}Response response=new $!{domainName}Response();
        logger.info("call $!{domainName}ServiceImpl({})",JsonMapper.nonEmptyMapper().toJson(request));
        try{


        response.setResultCodeAndMessage(BaseResponse.RESULT_CODE_SUCCESS,"调用成功!");
        return response;
        }catch(Exception e){

        logger.error("call $!{domainName}ServiceImpl接口异常!",e);
        response.setResultCodeAndMessage(BaseResponse.RESULT_CODE_SERVICE_UNAVAILABLE,"$!{domainName}ServiceImpl接口异常!"+e.getMessage());
        }
        return response;
        }

        }
