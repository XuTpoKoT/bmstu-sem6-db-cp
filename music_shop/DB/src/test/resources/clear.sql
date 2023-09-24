truncate public.user cascade;
truncate public.Card;
truncate public.Manufacturer cascade;
truncate public.Product cascade;
truncate public.DeliveryPoint cascade;
truncate public.Order_ cascade;
truncate public.Order_Product;
REASSIGN OWNED BY unregistered TO postgres;
--SELECT * FROM pg_depend WHERE objid = (SELECT oid FROM pg_roles WHERE rolname = 'unregistered');
DROP OWNED BY unregistered;
drop role if exists unregistered;
REASSIGN OWNED BY customer TO postgres;
--SELECT * FROM pg_depend WHERE objid = (SELECT oid FROM pg_roles WHERE rolname = 'customer');
DROP OWNED BY customer;
drop role if exists customer;
REASSIGN OWNED BY employee TO postgres;
--SELECT * FROM pg_depend WHERE objid = (SELECT oid FROM pg_roles WHERE rolname = 'employee');
DROP OWNED BY employee;
drop role if exists employee;

REASSIGN OWNED BY admin_ TO postgres;
DROP OWNED BY admin_;
drop role if exists admin_;






