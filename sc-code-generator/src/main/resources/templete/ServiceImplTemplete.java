#set($domain=$!domainName.substring(0,1).toLowerCase()+$!domainName.substring(1))
package $!{packageName};
import com.fg.commons.hessian.header.HeaderInfo;
import org.springframework.stereotype.Service;
import cjia.commons.base.validator.DataValidateException;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service("$!{domain}ServiceImpl")
public class $!{domainName}ServiceImpl implements I$!{domainName}Service{
     private static final Logger logger=LoggerFactory.getLogger($!{domainName}ServiceImpl.class);

@Override
public $!{domainName}Response call(HeaderInfo t,$!{domainName}Request request){
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

         @Override
         public void validate($!{domainName}Request request)throws DataValidateException{

          }
    }
