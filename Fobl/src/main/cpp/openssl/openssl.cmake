enable_language(ASM)

set(crypto_srcs
        crypto/aes/aes_cbc.c
        crypto/aes/aes_cfb.c
        crypto/aes/aes_ecb.c
        crypto/aes/aes_ige.c
        crypto/aes/aes_misc.c
        crypto/aes/aes_ofb.c
        crypto/aes/aes_wrap.c
        crypto/aria/aria.c
        crypto/asn1_dsa.c
        crypto/asn1/a_bitstr.c
        crypto/asn1/a_d2i_fp.c
        crypto/asn1/a_digest.c
        crypto/asn1/a_dup.c
        crypto/asn1/a_gentm.c
        crypto/asn1/a_i2d_fp.c
        crypto/asn1/a_int.c
        crypto/asn1/a_mbstr.c
        crypto/asn1/a_object.c
        crypto/asn1/a_octet.c
        crypto/asn1/a_print.c
        crypto/asn1/a_sign.c
        crypto/asn1/a_strex.c
        crypto/asn1/a_strnid.c
        crypto/asn1/a_time.c
        crypto/asn1/a_type.c
        crypto/asn1/a_utctm.c
        crypto/asn1/a_utf8.c
        crypto/asn1/a_verify.c
        crypto/asn1/ameth_lib.c
        crypto/asn1/asn1_err.c
        crypto/asn1/asn1_gen.c
        crypto/asn1/asn1_lib.c
        crypto/asn1/asn_mime.c
        crypto/asn1/asn_moid.c
        crypto/asn1/asn_mstbl.c
        crypto/asn1/asn_pack.c
        crypto/asn1/asn1_parse.c
        crypto/asn1/bio_asn1.c
        crypto/asn1/bio_ndef.c
        crypto/asn1/d2i_param.c
        crypto/asn1/d2i_pr.c
        crypto/asn1/d2i_pu.c
        crypto/asn1/evp_asn1.c
        crypto/asn1/f_int.c
        crypto/asn1/f_string.c
        crypto/asn1/i2d_evp.c
        crypto/asn1/n_pkey.c
        crypto/asn1/nsseq.c
        crypto/asn1/p5_pbe.c
        crypto/asn1/p5_pbev2.c
        crypto/asn1/p5_scrypt.c
        crypto/asn1/p8_pkey.c
        crypto/asn1/t_bitst.c
        crypto/asn1/t_pkey.c
        crypto/asn1/tasn_dec.c
        crypto/asn1/tasn_enc.c
        crypto/asn1/tasn_fre.c
        crypto/asn1/tasn_new.c
        crypto/asn1/tasn_prn.c
        crypto/asn1/tasn_scn.c
        crypto/asn1/tasn_typ.c
        crypto/asn1/tasn_utl.c
        crypto/asn1/x_algor.c
        crypto/asn1/x_bignum.c
        crypto/asn1/x_info.c
        crypto/asn1/x_int64.c
        crypto/asn1/x_long.c
        crypto/asn1/x_pkey.c
        crypto/asn1/x_sig.c
        crypto/asn1/x_spki.c
        crypto/asn1/x_val.c
        crypto/async/arch/async_null.c
        crypto/async/arch/async_posix.c
        crypto/async/async.c
        crypto/async/async_err.c
        crypto/async/async_wait.c

        crypto/bf/bf_cfb64.c
        crypto/bf/bf_ecb.c
        crypto/bf/bf_enc.c
        crypto/bf/bf_ofb64.c
        crypto/bf/bf_skey.c

        crypto/bio/bf_buff.c
        crypto/bio/bf_nbio.c
        crypto/bio/bf_null.c
        crypto/bio/bf_prefix.c
        crypto/bio/bf_readbuff.c
        crypto/bio/bio_addr.c
        crypto/bio/bio_dump.c
        crypto/bio/bio_print.c
        crypto/bio/bio_sock.c
        crypto/bio/bio_sock2.c
        crypto/bio/ossl_core_bio.c
        crypto/bio/bio_cb.c
        crypto/bio/bio_err.c
        crypto/bio/bio_lib.c
        crypto/bio/bio_meth.c
        crypto/bio/bss_acpt.c
        crypto/bio/bss_bio.c
        crypto/bio/bss_conn.c
        crypto/bio/bss_core.c
        crypto/bio/bss_dgram.c
        crypto/bio/bss_fd.c
        crypto/bio/bss_file.c
        crypto/bio/bss_log.c
        crypto/bio/bss_mem.c
        crypto/bio/bss_null.c
        crypto/bio/bss_sock.c
        crypto/bn/bn_add.c
        crypto/bn/bn_asm.c
        crypto/bn/bn_blind.c
        crypto/bn/bn_const.c
        crypto/bn/bn_conv.c
        crypto/bn/bn_ctx.c
        crypto/bn/bn_dh.c
        crypto/bn/bn_div.c
        crypto/bn/bn_err.c
        crypto/bn/bn_exp.c
        crypto/bn/bn_exp2.c
        crypto/bn/bn_rsa_fips186_4.c
        crypto/bn/bn_gcd.c
        crypto/bn/bn_gf2m.c
        crypto/bn/bn_intern.c
        crypto/bn/bn_kron.c
        crypto/bn/bn_lib.c
        crypto/bn/bn_mod.c
        crypto/bn/bn_mont.c
        crypto/bn/bn_mpi.c
        crypto/bn/bn_mul.c
        crypto/bn/bn_nist.c
        crypto/bn/bn_prime.c
        crypto/bn/bn_print.c
        crypto/bn/bn_rand.c
        crypto/bn/bn_recp.c
        crypto/bn/bn_shift.c
        crypto/bn/bn_sqr.c
        crypto/bn/bn_sqrt.c
        crypto/bn/bn_srp.c
        crypto/bn/bn_word.c
        crypto/bn/bn_x931p.c
        crypto/bn/rsaz_exp.c
        crypto/bsearch.c
        crypto/buffer/buf_err.c
        crypto/buffer/buffer.c
        crypto/chacha/chacha_enc.c
        crypto/cmac/cmac.c
        crypto/cms/cms_asn1.c
        crypto/cms/cms_att.c
        crypto/cms/cms_cd.c
        crypto/cms/cms_dd.c
        crypto/cms/cms_enc.c
        crypto/cms/cms_env.c
        crypto/cms/cms_err.c
        crypto/cms/cms_ess.c
        crypto/cms/cms_io.c
        crypto/cms/cms_kari.c
        crypto/cms/cms_lib.c
        crypto/cms/cms_pwri.c
        crypto/cms/cms_sd.c
        crypto/cms/cms_smime.c
        crypto/comp/c_zlib.c
        crypto/comp/comp_err.c
        crypto/comp/comp_lib.c
        crypto/cmp/cmp_util.c
        crypto/cmp/cmp_err.c
        crypto/conf/conf_api.c
        crypto/conf/conf_def.c
        crypto/conf/conf_err.c
        crypto/conf/conf_lib.c
        crypto/conf/conf_mall.c
        crypto/conf/conf_mod.c
        crypto/conf/conf_sap.c
        crypto/conf/conf_ssl.c
        crypto/context.c
        crypto/core_algorithm.c
        crypto/core_fetch.c
        crypto/core_namemap.c
        crypto/cpt_err.c
        crypto/crmf/crmf_asn.c
        crypto/crmf/crmf_err.c
        crypto/crmf/crmf_lib.c
        crypto/crmf/crmf_pbm.c
        crypto/cryptlib.c
        crypto/ct/ct_b64.c
        crypto/ct/ct_err.c
        crypto/ct/ct_log.c
        crypto/ct/ct_oct.c
        crypto/ct/ct_policy.c
        crypto/ct/ct_prn.c
        crypto/ct/ct_sct.c
        crypto/ct/ct_sct_ctx.c
        crypto/ct/ct_vfy.c
        crypto/ct/ct_x509v3.c
        crypto/ctype.c
        crypto/cpuid.c
        crypto/cversion.c
        crypto/des/cbc_cksm.c
        crypto/des/cbc_enc.c
        crypto/des/cfb64ede.c
        crypto/des/cfb64enc.c
        crypto/des/cfb_enc.c
        crypto/des/des_enc.c
        crypto/des/ecb3_enc.c
        crypto/des/ecb_enc.c
        crypto/des/fcrypt.c
        crypto/des/fcrypt_b.c
        crypto/des/ofb64ede.c
        crypto/des/ofb64enc.c
        crypto/des/ofb_enc.c
        crypto/des/pcbc_enc.c
        crypto/des/qud_cksm.c
        crypto/des/rand_key.c
        crypto/des/set_key.c
        crypto/des/str2key.c
        crypto/des/xcbc_enc.c
        crypto/dh/dh_ameth.c
        crypto/dh/dh_asn1.c
        crypto/dh/dh_backend.c
        crypto/dh/dh_check.c
        crypto/dh/dh_depr.c
        crypto/dh/dh_err.c
        crypto/dh/dh_gen.c
        crypto/dh/dh_group_params.c
        crypto/dh/dh_kdf.c
        crypto/dh/dh_key.c
        crypto/dh/dh_lib.c
        crypto/dh/dh_meth.c
        crypto/dh/dh_pmeth.c
        crypto/dh/dh_rfc5114.c
        crypto/dsa/dsa_ameth.c
        crypto/dsa/dsa_asn1.c
        crypto/dsa/dsa_backend.c
        crypto/dsa/dsa_check.c
        crypto/dsa/dsa_depr.c
        crypto/dsa/dsa_err.c
        crypto/dsa/dsa_gen.c
        crypto/dsa/dsa_key.c
        crypto/dsa/dsa_lib.c
        crypto/dsa/dsa_meth.c
        crypto/dsa/dsa_ossl.c
        crypto/dsa/dsa_pmeth.c
        crypto/dsa/dsa_prn.c
        crypto/dsa/dsa_sign.c
        crypto/dsa/dsa_vrf.c
        crypto/der_writer.c
        crypto/dso/dso_dl.c
        crypto/dso/dso_dlfcn.c
        crypto/dso/dso_err.c
        crypto/dso/dso_lib.c
        crypto/dso/dso_openssl.c
        crypto/ebcdic.c
        crypto/ec/curve25519.c
        crypto/ec/curve448/arch_32/f_impl32.c
        crypto/ec/curve448/arch_64/f_impl64.c
        crypto/ec/curve448/curve448_tables.c
        crypto/ec/curve448/curve448.c
        crypto/ec/curve448/eddsa.c
        crypto/ec/curve448/scalar.c
        crypto/ec/curve448/f_generic.c
        crypto/ec/ec2_oct.c
        crypto/ec/ec2_smpl.c
        crypto/ec/ec_ameth.c
        crypto/ec/ec_asn1.c
        crypto/ec/ec_backend.c
        crypto/ec/ec_check.c
        crypto/ec/ec_curve.c
        crypto/ec/ec_cvt.c
        crypto/ec/ec_err.c
        crypto/ec/ec_key.c
        crypto/ec/ec_kmeth.c
        crypto/ec/ec_lib.c
        crypto/ec/ec_mult.c
        crypto/ec/ec_oct.c
        crypto/ec/ec_pmeth.c
        crypto/ec/ec_print.c
        crypto/ec/ecdh_kdf.c
        crypto/ec/ecdh_ossl.c
        crypto/ec/ecdsa_ossl.c
        crypto/ec/ecdsa_sign.c
        crypto/ec/ecdsa_vrf.c
        crypto/ec/eck_prn.c
        crypto/ec/ecp_mont.c
        crypto/ec/ecp_nist.c
        crypto/ec/ecp_nistz256.c
        crypto/ec/ecp_oct.c
        crypto/ec/ecp_smpl.c
        crypto/ec/ecx_backend.c
        crypto/ec/ecx_meth.c
        crypto/ec/ecx_key.c
        crypto/encode_decode/decoder_err.c
        crypto/encode_decode/decoder_lib.c
        crypto/encode_decode/decoder_meth.c
        crypto/encode_decode/decoder_pkey.c
        crypto/encode_decode/encoder_err.c
        crypto/encode_decode/encoder_lib.c
        crypto/encode_decode/encoder_meth.c
        crypto/encode_decode/encoder_pkey.c
        crypto/engine/eng_all.c
        crypto/engine/eng_cnf.c
        crypto/engine/eng_ctrl.c
        crypto/engine/eng_dyn.c
        crypto/engine/eng_err.c
        crypto/engine/eng_fat.c
        crypto/engine/eng_init.c
        crypto/engine/eng_lib.c
        crypto/engine/eng_list.c
        crypto/engine/eng_openssl.c
        crypto/engine/eng_pkey.c
        crypto/engine/eng_table.c
        crypto/engine/tb_asnmth.c
        crypto/engine/tb_cipher.c
        crypto/engine/tb_dh.c
        crypto/engine/tb_digest.c
        crypto/engine/tb_dsa.c
        crypto/engine/tb_eckey.c
        crypto/engine/tb_pkmeth.c
        crypto/engine/tb_rand.c
        crypto/engine/tb_rsa.c
        crypto/err/err.c
        crypto/err/err_all.c
        crypto/err/err_blocks.c
        crypto/err/err_prn.c
        crypto/ess/ess_asn1.c
        crypto/ess/ess_err.c
        crypto/ess/ess_lib.c
        crypto/evp/asymcipher.c
        crypto/evp/bio_b64.c
        crypto/evp/bio_enc.c
        crypto/evp/bio_md.c
        crypto/evp/bio_ok.c
        crypto/evp/c_allc.c
        crypto/evp/c_alld.c
        crypto/evp/cmeth_lib.c
        crypto/evp/ctrl_params_translate.c
        crypto/evp/dh_ctrl.c
        crypto/evp/dh_support.c
        crypto/evp/digest.c
        crypto/evp/dsa_ctrl.c
        crypto/evp/e_aes.c
        crypto/evp/e_aes_cbc_hmac_sha1.c
        crypto/evp/e_aes_cbc_hmac_sha256.c
        crypto/evp/e_aria.c
        crypto/evp/e_bf.c
        crypto/evp/e_chacha20_poly1305.c
        crypto/evp/e_des.c
        crypto/evp/e_des3.c
        crypto/evp/e_null.c
        crypto/evp/e_old.c
        crypto/evp/e_rc2.c
        crypto/evp/e_rc4.c
        crypto/evp/e_rc4_hmac_md5.c
        crypto/evp/e_rc5.c
        crypto/evp/e_sm4.c
        crypto/evp/e_xcbc_d.c
        crypto/evp/ec_ctrl.c
        crypto/evp/ec_support.c
        crypto/evp/encode.c
        crypto/evp/evp_cnf.c
        crypto/evp/evp_enc.c
        crypto/evp/evp_err.c
        crypto/evp/evp_fetch.c
        crypto/evp/evp_key.c
        crypto/evp/evp_lib.c
        crypto/evp/evp_pbe.c
        crypto/evp/evp_pkey.c
        crypto/evp/evp_utils.c
        crypto/evp/exchange.c
        crypto/evp/kdf_lib.c
        crypto/evp/kdf_meth.c
        crypto/evp/kem.c
        crypto/evp/keymgmt_lib.c
        crypto/evp/keymgmt_meth.c
        crypto/evp/legacy_blake2.c
        crypto/evp/legacy_md4.c
        crypto/evp/legacy_md5.c
        crypto/evp/legacy_md5_sha1.c
        crypto/evp/legacy_sha.c
        crypto/evp/mac_lib.c
        crypto/evp/mac_meth.c
        crypto/evp/m_null.c
        crypto/evp/m_sigver.c
        crypto/evp/names.c
        crypto/evp/p5_crpt.c
        crypto/evp/p5_crpt2.c
        crypto/evp/pbe_scrypt.c
        crypto/evp/p_dec.c
        crypto/evp/p_enc.c
        crypto/evp/p_legacy.c
        crypto/evp/p_lib.c
        crypto/evp/p_open.c
        crypto/evp/p_seal.c
        crypto/evp/p_sign.c
        crypto/evp/p_verify.c
        crypto/evp/pmeth_check.c
        crypto/evp/pmeth_gn.c
        crypto/evp/pmeth_lib.c
        crypto/evp/evp_rand.c
        crypto/evp/signature.c
        crypto/ex_data.c
        crypto/ffc/ffc_backend.c
        crypto/ffc/ffc_dh.c
        crypto/ffc/ffc_key_generate.c
        crypto/ffc/ffc_key_validate.c
        crypto/ffc/ffc_params.c
        crypto/ffc/ffc_params_generate.c
        crypto/ffc/ffc_params_validate.c
        crypto/getenv.c
        crypto/hmac/hmac.c
        crypto/http/http_client.c
        crypto/http/http_err.c
        crypto/http/http_lib.c
        crypto/info.c
        crypto/init.c
        crypto/initthread.c
        crypto/kdf/kdf_err.c
        crypto/lhash/lh_stats.c
        crypto/lhash/lhash.c
        crypto/md4/md4_dgst.c
        crypto/md4/md4_one.c
        crypto/md5/md5_dgst.c
        crypto/md5/md5_one.c
        crypto/md5/md5_sha1.c
        crypto/mem.c
        crypto/mem_sec.c
        crypto/modes/cbc128.c
        crypto/modes/ccm128.c
        crypto/modes/cfb128.c
        crypto/modes/ctr128.c
        crypto/modes/gcm128.c
        crypto/modes/ocb128.c
        crypto/modes/ofb128.c
        crypto/modes/siv128.c
        crypto/modes/xts128.c
        crypto/modes/wrap128.c
        crypto/o_dir.c
        crypto/o_fopen.c
        crypto/o_init.c
        crypto/o_str.c
        crypto/o_time.c
        crypto/objects/o_names.c
        crypto/objects/obj_dat.c
        crypto/objects/obj_err.c
        crypto/objects/obj_lib.c
        crypto/objects/obj_xref.c
        crypto/ocsp/ocsp_asn.c
        crypto/ocsp/ocsp_cl.c
        crypto/ocsp/ocsp_err.c
        crypto/ocsp/ocsp_ext.c
        crypto/ocsp/ocsp_lib.c
        crypto/ocsp/ocsp_prn.c
        crypto/ocsp/ocsp_srv.c
        crypto/ocsp/ocsp_vfy.c
        crypto/ocsp/v3_ocsp.c
        crypto/packet.c
        crypto/params.c
        crypto/params_dup.c
        crypto/param_build.c
        crypto/param_build_set.c
        crypto/params_from_text.c
        crypto/passphrase.c
        crypto/pem/pem_all.c
        crypto/pem/pem_err.c
        crypto/pem/pem_info.c
        crypto/pem/pem_lib.c
        crypto/pem/pem_oth.c
        crypto/pem/pem_pk8.c
        crypto/pem/pem_pkey.c
        crypto/pem/pem_sign.c
        crypto/pem/pem_x509.c
        crypto/pem/pem_xaux.c
        crypto/pem/pvkfmt.c
        crypto/pkcs12/p12_add.c
        crypto/pkcs12/p12_asn.c
        crypto/pkcs12/p12_attr.c
        crypto/pkcs12/p12_crpt.c
        crypto/pkcs12/p12_crt.c
        crypto/pkcs12/p12_decr.c
        crypto/pkcs12/p12_init.c
        crypto/pkcs12/p12_key.c
        crypto/pkcs12/p12_kiss.c
        crypto/pkcs12/p12_mutl.c
        crypto/pkcs12/p12_npas.c
        crypto/pkcs12/p12_p8d.c
        crypto/pkcs12/p12_p8e.c
        crypto/pkcs12/p12_sbag.c
        crypto/pkcs12/p12_utl.c
        crypto/pkcs12/pk12err.c
        crypto/pkcs7/pk7_asn1.c
        crypto/pkcs7/pk7_attr.c
        crypto/pkcs7/pk7_doit.c
        crypto/pkcs7/pk7_lib.c
        crypto/pkcs7/pk7_mime.c
        crypto/pkcs7/pk7_smime.c
        crypto/pkcs7/pkcs7err.c
        crypto/poly1305/poly1305.c
        crypto/provider.c
        crypto/provider_core.c
        crypto/provider_conf.c
        crypto/provider_predefined.c
        crypto/property/defn_cache.c
        crypto/property/property.c
        crypto/property/property_err.c
        crypto/property/property_parse.c
        crypto/property/property_query.c
        crypto/property/property_string.c
        crypto/provider_child.c
        crypto/punycode.c
        crypto/rand/prov_seed.c
        crypto/rand/rand_egd.c
        crypto/rand/rand_err.c
        crypto/rand/rand_lib.c
        crypto/rand/rand_meth.c
        crypto/rand/rand_pool.c
        crypto/rand/randfile.c
        crypto/rc2/rc2_cbc.c
        crypto/rc2/rc2_ecb.c
        crypto/rc2/rc2_skey.c
        crypto/rc2/rc2cfb64.c
        crypto/rc2/rc2ofb64.c
        crypto/rc4/rc4_enc.c
        crypto/rc4/rc4_skey.c
        crypto/rsa/rsa_ameth.c
        crypto/rsa/rsa_asn1.c
        crypto/rsa/rsa_backend.c
        crypto/rsa/rsa_chk.c
        crypto/rsa/rsa_crpt.c
        crypto/rsa/rsa_err.c
        crypto/rsa/rsa_gen.c
        crypto/rsa/rsa_lib.c
        crypto/rsa/rsa_meth.c
        crypto/rsa/rsa_mp.c
        crypto/rsa/rsa_mp_names.c
        crypto/rsa/rsa_none.c
        crypto/rsa/rsa_oaep.c
        crypto/rsa/rsa_ossl.c
        crypto/rsa/rsa_pk1.c
        crypto/rsa/rsa_pmeth.c
        crypto/rsa/rsa_prn.c
        crypto/rsa/rsa_pss.c
        crypto/rsa/rsa_saos.c
        crypto/rsa/rsa_schemes.c
        crypto/rsa/rsa_sign.c
        crypto/rsa/rsa_sp800_56b_check.c
        crypto/rsa/rsa_sp800_56b_gen.c
        crypto/rsa/rsa_x931.c
        crypto/rsa/rsa_x931g.c
        crypto/self_test_core.c
        crypto/sha/sha1_one.c
        crypto/sha/sha1dgst.c
        crypto/sha/sha256.c
        crypto/sha/sha3.c
        crypto/sha/sha512.c
        crypto/siphash/siphash.c
        crypto/sm2/sm2_err.c
        crypto/sm2/sm2_crypt.c
        crypto/sm2/sm2_key.c
        crypto/sm2/sm2_sign.c
        crypto/sm3/sm3.c
        crypto/sm3/legacy_sm3.c
        crypto/sm4/sm4.c
        crypto/sparse_array.c
        crypto/srp/srp_lib.c
        crypto/srp/srp_vfy.c
        crypto/store/store_init.c
        crypto/store/store_err.c
        crypto/store/store_register.c
        crypto/store/store_result.c
        crypto/store/store_lib.c
        crypto/store/store_meth.c
        crypto/store/store_strings.c
        crypto/stack/stack.c
        crypto/threads_none.c
        crypto/threads_pthread.c
        crypto/threads_win.c
        crypto/threads_lib.c
        crypto/trace.c
        crypto/ts/ts_err.c
        crypto/txt_db/txt_db.c
        crypto/ui/ui_err.c
        crypto/ui/ui_lib.c
        crypto/ui/ui_null.c
        crypto/ui/ui_openssl.c
        crypto/ui/ui_util.c
        crypto/uid.c
        crypto/x509/by_dir.c
        crypto/x509/by_file.c
        crypto/x509/by_store.c
        crypto/x509/t_crl.c
        crypto/x509/t_req.c
        crypto/x509/t_x509.c
        crypto/x509/x509_att.c
        crypto/x509/x509_cmp.c
        crypto/x509/x509_d2.c
        crypto/x509/x509_def.c
        crypto/x509/x509_err.c
        crypto/x509/x509_ext.c
        crypto/x509/x509_lu.c
        crypto/x509/x509_obj.c
        crypto/x509/x509_r2x.c
        crypto/x509/x509_req.c
        crypto/x509/x509_set.c
        crypto/x509/x509_trust.c
        crypto/x509/x509_txt.c
        crypto/x509/x509_v3.c
        crypto/x509/x509_vfy.c
        crypto/x509/x509_vpm.c
        crypto/x509/x509cset.c
        crypto/x509/x509name.c
        crypto/x509/x509rset.c
        crypto/x509/x509spki.c
        crypto/x509/x509type.c
        crypto/x509/x_all.c
        crypto/x509/x_attrib.c
        crypto/x509/x_crl.c
        crypto/x509/x_exten.c
        crypto/x509/x_name.c
        crypto/x509/x_pubkey.c
        crypto/x509/x_req.c
        crypto/x509/x_x509.c
        crypto/x509/x_x509a.c
        crypto/x509/pcy_cache.c
        crypto/x509/pcy_data.c
        crypto/x509/pcy_lib.c
        crypto/x509/pcy_map.c
        crypto/x509/pcy_node.c
        crypto/x509/pcy_tree.c
        crypto/x509/v3_admis.c
        crypto/x509/v3_akeya.c
        crypto/x509/v3_akid.c
        crypto/x509/v3_bcons.c
        crypto/x509/v3_bitst.c
        crypto/x509/v3_conf.c
        crypto/x509/v3_cpols.c
        crypto/x509/v3_crld.c
        crypto/x509/v3_enum.c
        crypto/x509/v3_extku.c
        crypto/x509/v3_genn.c
        crypto/x509/v3_ia5.c
        crypto/x509/v3_info.c
        crypto/x509/v3_int.c
        crypto/x509/v3_ist.c
        crypto/x509/v3_lib.c
        crypto/x509/v3_ncons.c
        crypto/x509/v3_pci.c
        crypto/x509/v3_pcia.c
        crypto/x509/v3_pcons.c
        crypto/x509/v3_pku.c
        crypto/x509/v3_pmaps.c
        crypto/x509/v3_prn.c
        crypto/x509/v3_purp.c
        crypto/x509/v3_san.c
        crypto/x509/v3_skid.c
        crypto/x509/v3_sxnet.c
        crypto/x509/v3_tlsf.c
        crypto/x509/v3_utf8.c
        crypto/x509/v3_utl.c
        crypto/x509/v3err.c
        )
if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    set(crypto_srcs ${crypto_srcs}
            crypto/aes/asm/aes-armv4.S
            crypto/aes/asm/aesv8-armx.S
            crypto/aes/asm/bsaes-armv7.S
            crypto/armcap.c
            crypto/armv4cpuid.S
            crypto/bn/asm/armv4-gf2m.S
            crypto/bn/asm/armv4-mont.S
            crypto/ec/asm/ecp_nistz256-armv4.S
            crypto/modes/asm/ghash-armv4.S
            crypto/modes/asm/ghashv8-armx.S
            crypto/sha/asm/sha1-armv4-large.S
            crypto/sha/asm/sha256-armv4.S
            crypto/sha/asm/sha512-armv4.S
            crypto/sha/asm/keccak1600-armv4.S
            )

elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    set(crypto_srcs ${crypto_srcs}
            crypto/aes/aes_core.c
            crypto/aes/asm/aesv8-armx-64.S
            crypto/aes/asm/vpaes-armv8.S
            crypto/arm64cpuid.S
            crypto/armcap.c
            crypto/bn/asm/armv8-mont.S
            crypto/ec/asm/ecp_nistz256-armv8.S
            crypto/modes/asm/ghashv8-armx-64.S
            crypto/modes/asm/aes-gcm-armv8_64.S
            crypto/poly1305/asm/poly1305-armv8.S
            crypto/sha/asm/sha1-armv8.S
            crypto/sha/asm/sha256-armv8.S
            crypto/sha/asm/sha512-armv8.S
            crypto/sha/asm/keccak1600-armv8.S
            )
elseif (${ANDROID_ABI} STREQUAL "x86")
    set(crypto_srcs ${crypto_srcs}
            crypto/aes/aes_x86core.c
            crypto/aes/asm/aesni-x86.S
            crypto/aes/asm/vpaes-x86.S
            crypto/bf/asm/bf-586.S
            crypto/bn/asm/bn-586.S
            crypto/bn/asm/co-586.S
            crypto/bn/asm/x86-gf2m.S
            crypto/bn/asm/x86-mont.S
            crypto/des/asm/crypt586.S
            crypto/des/asm/des-586.S
            crypto/ec/asm/ecp_nistz256-x86.S
            crypto/md5/asm/md5-586.S
            crypto/modes/asm/ghash-x86.S
            crypto/poly1305/asm/poly1305-x86.S
            crypto/sha/asm/sha1-586.S
            crypto/sha/asm/sha256-586.S
            crypto/sha/asm/sha512-586.S
            crypto/sha/asm/keccak1600-mmx.S
            crypto/x86cpuid.S
            )
    list(REMOVE_ITEM crypto_srcs
            crypto/bf/bf_enc.c
            crypto/bn/bn_asm.c
            crypto/des/des_enc.c
            crypto/des/fcrypt_b.c
            )
elseif (${ANDROID_ABI} STREQUAL "x86_64")
    set(crypto_srcs ${crypto_srcs}
            crypto/aes/asm/aes-x86_64.S
            crypto/aes/asm/aesni-mb-x86_64.S
            crypto/aes/asm/aesni-sha1-x86_64.S
            crypto/aes/asm/aesni-sha256-x86_64.S
            crypto/aes/asm/aesni-x86_64.S
            crypto/aes/asm/bsaes-x86_64.S
            crypto/aes/asm/vpaes-x86_64.S
            crypto/bn/asm/rsaz-avx2.S
            crypto/bn/asm/rsaz-x86_64.S
            crypto/bn/asm/x86_64-gcc.c
            crypto/bn/asm/x86_64-gf2m.S
            crypto/bn/asm/x86_64-mont.S
            crypto/bn/asm/x86_64-mont5.S
            crypto/ec/asm/ecp_nistz256-x86_64.S
            crypto/md5/asm/md5-x86_64.S
            crypto/modes/asm/aesni-gcm-x86_64.S
            crypto/modes/asm/ghash-x86_64.S
            crypto/poly1305/asm/poly1305-x86_64.S
            crypto/rc4/asm/rc4-md5-x86_64.S
            crypto/rc4/asm/rc4-x86_64.S
            crypto/sha/asm/sha1-mb-x86_64.S
            crypto/sha/asm/sha1-x86_64.S
            crypto/sha/asm/sha256-mb-x86_64.S
            crypto/sha/asm/sha256-x86_64.S
            crypto/sha/asm/sha512-x86_64.S
            crypto/sha/asm/keccak1600-x86_64.S
            #crypto/sha/asm/keccak1600-avx2.S
            #crypto/sha/asm/keccak1600-avx512.S
            #crypto/sha/asm/keccak1600-avx512vl.S
            crypto/ec/asm/x25519-x86_64.S
            crypto/x86_64cpuid.S
            )

    list(REMOVE_ITEM crypto_srcs
            crypto/aes/aes_cbc.c
            crypto/bn/bn_asm.c
            crypto/mem_clr.c
            crypto/rc4/rc4_enc.c
            crypto/rc4/rc4_skey.c
            )
else ()
    message(FATAL_ERROR "Unknown arch ${ANDROID_ABI} for source files")
endif ()

set(provider_srcs
        providers/nullprov.c
        providers/common/bio_prov.c
        providers/common/capabilities.c
        providers/common/der/der_digests_gen.c
        providers/common/der/der_dsa_gen.c
        providers/common/der/der_dsa_sig.c
        providers/common/der/der_ec_gen.c
        providers/common/der/der_ecx_gen.c
        providers/common/der/der_ecx_key.c
        providers/common/der/der_ec_sig.c
        providers/common/der/der_rsa_gen.c
        providers/common/der/der_rsa_key.c
        providers/common/der/der_rsa_sig.c
        providers/common/der/der_sm2_gen.c
        providers/common/der/der_sm2_sig.c
        providers/common/der/der_wrap_gen.c
        providers/common/digest_to_nid.c
        providers/common/provider_ctx.c
        providers/common/provider_util.c
        providers/common/provider_err.c
        providers/common/provider_seeding.c
        providers/common/securitycheck.c
        providers/common/securitycheck_default.c
        providers/baseprov.c
        providers/defltprov.c
        providers/implementations/asymciphers/rsa_enc.c
        providers/implementations/asymciphers/sm2_enc.c
        providers/implementations/ciphers/ciphercommon_block.c
        providers/implementations/ciphers/cipher_chacha20.c
        providers/implementations/ciphers/cipher_aes_cbc_hmac_sha1_hw.c
        providers/implementations/ciphers/cipher_aes_cbc_hmac_sha.c
        providers/implementations/ciphers/cipher_tdes_wrap.c
        providers/implementations/ciphers/cipher_aes.c
        providers/implementations/ciphers/cipher_blowfish_hw.c
        providers/implementations/ciphers/cipher_aes_wrp.c
        providers/implementations/ciphers/cipher_des.c
        providers/implementations/ciphers/ciphercommon_gcm_hw.c
        providers/implementations/ciphers/cipher_aria.c
        providers/implementations/ciphers/cipher_aes_ocb.c
        providers/implementations/ciphers/cipher_desx_hw.c
        providers/implementations/ciphers/cipher_aes_xts.c
        providers/implementations/ciphers/cipher_aria_hw.c
        providers/implementations/ciphers/cipher_aes_gcm.c
        providers/implementations/ciphers/ciphercommon_ccm_hw.c
        providers/implementations/ciphers/cipher_aes_siv.c
        providers/implementations/ciphers/cipher_tdes_common.c
        providers/implementations/ciphers/cipher_aes_ccm.c
        providers/implementations/ciphers/cipher_sm4.c
        providers/implementations/ciphers/cipher_aes_hw.c
        providers/implementations/ciphers/cipher_aes_ocb_hw.c
        providers/implementations/ciphers/cipher_cts.c
        providers/implementations/ciphers/cipher_des_hw.c
        providers/implementations/ciphers/cipher_null.c
        providers/implementations/ciphers/cipher_rc2_hw.c
        providers/implementations/ciphers/cipher_chacha20_poly1305_hw.c
        providers/implementations/ciphers/cipher_tdes.c
        providers/implementations/ciphers/cipher_aes_ccm_hw.c
        providers/implementations/ciphers/ciphercommon_ccm.c
        providers/implementations/ciphers/cipher_tdes_default.c
        providers/implementations/ciphers/ciphercommon.c
        providers/implementations/ciphers/ciphercommon_block.c
        providers/implementations/ciphers/cipher_tdes_default_hw.c
        providers/implementations/ciphers/cipher_sm4_hw.c
        providers/implementations/ciphers/ciphercommon_gcm.c
        providers/implementations/ciphers/cipher_tdes_wrap_hw.c
        providers/implementations/ciphers/cipher_desx.c
        providers/implementations/ciphers/ciphercommon_hw.c
        providers/implementations/ciphers/cipher_rc2.c
        providers/implementations/ciphers/cipher_aes_xts_hw.c
        providers/implementations/ciphers/cipher_chacha20_hw.c
        providers/implementations/ciphers/cipher_aria_ccm_hw.c
        providers/implementations/ciphers/cipher_blowfish.c
        providers/implementations/ciphers/cipher_aria_gcm.c
        providers/implementations/ciphers/cipher_aria_gcm_hw.c
        providers/implementations/ciphers/cipher_rc4_hw.c
        providers/implementations/ciphers/cipher_chacha20_poly1305.c
        providers/implementations/ciphers/cipher_rc4_hmac_md5.c
        providers/implementations/ciphers/cipher_aes_cbc_hmac_sha256_hw.c
        providers/implementations/ciphers/cipher_aria_ccm.c
        providers/implementations/ciphers/cipher_aes_siv_hw.c
        providers/implementations/ciphers/cipher_aes_gcm_hw.c
        providers/implementations/ciphers/cipher_tdes_hw.c
        providers/implementations/ciphers/cipher_aes_xts_fips.c
        providers/implementations/digests/blake2_prov.c
        providers/implementations/digests/blake2s_prov.c
        providers/implementations/digests/blake2b_prov.c
        providers/implementations/digests/digestcommon.c
        providers/implementations/digests/md4_prov.c
        providers/implementations/digests/md5_prov.c
        providers/implementations/digests/md5_sha1_prov.c
        providers/implementations/digests/null_prov.c
        providers/implementations/digests/sha2_prov.c
        providers/implementations/digests/sha3_prov.c
        providers/implementations/digests/sm3_prov.c
        providers/implementations/encode_decode/decode_der2key.c
        providers/implementations/encode_decode/decode_epki2pki.c
        providers/implementations/encode_decode/decode_spki2typespki.c
        providers/implementations/encode_decode/encode_key2any.c
        providers/implementations/encode_decode/encode_key2blob.c
        providers/implementations/encode_decode/encode_key2ms.c
        providers/implementations/encode_decode/encode_key2text.c
        providers/implementations/encode_decode/decode_msblob2key.c
        providers/implementations/encode_decode/decode_pem2der.c
        providers/implementations/encode_decode/decode_pvk2key.c
        providers/implementations/encode_decode/endecoder_common.c
        providers/implementations/exchange/ecx_exch.c
        providers/implementations/exchange/ecdh_exch.c
        providers/implementations/exchange/dh_exch.c
        providers/implementations/exchange/kdf_exch.c
        providers/implementations/kem/rsa_kem.c
        providers/implementations/keymgmt/dh_kmgmt.c
        providers/implementations/keymgmt/ec_kmgmt.c
        providers/implementations/keymgmt/dsa_kmgmt.c
        providers/implementations/keymgmt/ecx_kmgmt.c
        providers/implementations/keymgmt/kdf_legacy_kmgmt.c
        providers/implementations/keymgmt/mac_legacy_kmgmt.c
        providers/implementations/keymgmt/rsa_kmgmt.c
        providers/implementations/kdfs/x942kdf.c
        providers/implementations/kdfs/sskdf.c
        providers/implementations/kdfs/tls1_prf.c
        providers/implementations/kdfs/sshkdf.c
        providers/implementations/kdfs/scrypt.c
        providers/implementations/kdfs/krb5kdf.c
        providers/implementations/kdfs/hkdf.c
        providers/implementations/kdfs/pbkdf2_fips.c
        providers/implementations/kdfs/kbkdf.c
        providers/implementations/kdfs/pbkdf2.c
        providers/implementations/kdfs/pkcs12kdf.c
        providers/implementations/macs/blake2b_mac.c
        providers/implementations/macs/blake2s_mac.c
        providers/implementations/macs/cmac_prov.c
        providers/implementations/macs/gmac_prov.c
        providers/implementations/macs/hmac_prov.c
        providers/implementations/macs/kmac_prov.c
        providers/implementations/macs/poly1305_prov.c
        providers/implementations/macs/siphash_prov.c
        providers/implementations/rands/drbg.c
        providers/implementations/rands/drbg_ctr.c
        providers/implementations/rands/drbg_hash.c
        providers/implementations/rands/drbg_hmac.c
        providers/implementations/rands/seed_src.c
        providers/implementations/rands/seeding/rand_unix.c
        providers/implementations/rands/test_rng.c
        providers/implementations/signature/dsa_sig.c
        providers/implementations/signature/ecdsa_sig.c
        providers/implementations/signature/eddsa_sig.c
        providers/implementations/signature/mac_legacy_sig.c
        providers/implementations/signature/rsa_sig.c
	    providers/implementations/signature/sm2_sig.c
        providers/implementations/storemgmt/file_store.c
        providers/implementations/storemgmt/file_store_any2obj.c
        providers/prov_running.c
        )

set(legacy_srcs
        providers/legacyprov.c
        providers/implementations/ciphers/cipher_rc4_hmac_md5_hw.c
        providers/implementations/ciphers/cipher_rc4_hmac_md5.c
        providers/implementations/ciphers/cipher_rc4.c
        providers/implementations/kdfs/pbkdf1.c
)

PREPEND(crypto_srcs_with_path ${OPENSSL_PATH} ${provider_srcs} ${legacy_srcs} ${crypto_srcs})
add_library(crypto ${SSLLIBTYPE} ${crypto_srcs_with_path})

target_include_directories(crypto PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/crypto/include
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/include
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/crypto/include
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/crypto/
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/crypto/ec/curve448/arch_32
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/crypto/ec/curve448/
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/providers/common/include/
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/providers/implementations/include/
        )

target_include_directories(crypto PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/crypto/modes

        )

target_compile_definitions(crypto PRIVATE -DNO_WINDOWS_BRAINDEATH -DMODULESDIR="ossl-modules" -DOPENSSL_BUILDING_OPENSSL)
target_compile_options(crypto PRIVATE -Wno-missing-field-initializers -Wno-unused-parameter
        -DKECCAK1600_ASM
        -DNDEBUG
        -DECP_NISTZ256_ASM
        -DSHA1_ASM
        -DSHA256_ASM
        -DSHA512_ASM
        -DOPENSSL_PIC
        -DOPENSSL_THREADS
        -DOPENSSL_CPUID_OBJ
        -DL_ENDIAN
        -DSTATIC_LEGACY
        )

if (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    target_compile_definitions(crypto PRIVATE
            -DAES_ASM
            -DBSAES_ASM
            -DGHASH_ASM
            -DOPENSSL_BN_ASM_GF2m
            )


elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    target_compile_definitions(crypto PRIVATE
            -DPOLY1305_ASM
            -DVPAES_ASM
            )
elseif (${ANDROID_ABI} STREQUAL "x86")
    target_compile_definitions(crypto PRIVATE
            -DAES_ASM
            -DDES_ASM
            -DECP_NISTZ256_ASM
            -DGHASH_ASM
            -DMD5_ASM
            -DOPENSSL_BN_ASM_GF2m
            -DOPENSSL_BN_ASM_PART_WORDS
            -DOPENSSL_IA32_SSE2
            -DPOLY1305_ASM
            -DVPAES_ASM
            )
elseif (${ANDROID_ABI} STREQUAL "x86_64")
    target_compile_definitions(crypto PRIVATE
            -DAES_ASM
            -DBSAES_ASM
            -DECP_NISTZ256_ASM
            -DGHASH_ASM
            -DMD5_ASM
            -DNDEBUG
            -DOPENSSL_BN_ASM_GF2m
            -DOPENSSL_BN_ASM_MONT5
            -DOPENSSL_IA32_SSE2
            -DPOLY1305_ASM
            -DVPAES_ASM
            -DX25519_ASM
            )
else ()
    message(FATAL_ERROR "Unknown arch ${ANDROID_ABI} for flags")
endif ()

if (${ANDROID_ABI} STREQUAL "x86_64" OR ${ANDROID_ABI} STREQUAL "arm64-v8a")
    target_compile_definitions(crypto PRIVATE
            -DOPENSSLDIR=\"/system/lib/ssl\"
            -DENGINESDIR=\"/system/lib/ssl/engines\"
            )
else ()
    target_compile_definitions(crypto PRIVATE
            -DOPENSSLDIR=\"/system/lib64/ssl\"
            -DENGINESDIR=\"/system/lib64/ssl/engines\"
            )
endif ()


################## SSL Library ###########################################

set(ssl_srcs
        ssl/bio_ssl.c
        ssl/d1_lib.c
        ssl/d1_msg.c
        ssl/d1_srtp.c
        ssl/methods.c
        ssl/pqueue.c
        ssl/record/dtls1_bitmap.c
        ssl/record/rec_layer_d1.c
        ssl/record/rec_layer_s3.c
        ssl/record/ssl3_buffer.c
        ssl/record/ssl3_record.c
        ssl/record/ssl3_record_tls13.c
        ssl/record/tls_pad.c
        ssl/s3_cbc.c
        ssl/s3_enc.c
        ssl/s3_lib.c
        ssl/s3_msg.c
        ssl/ssl_asn1.c
        ssl/ssl_cert.c
        ssl/ssl_ciph.c
        ssl/ssl_conf.c
        ssl/ssl_err.c
        ssl/ssl_init.c
        ssl/ssl_lib.c
        ssl/ssl_mcnf.c
        ssl/ssl_rsa.c
        ssl/ssl_rsa_legacy.c
        ssl/ssl_sess.c
        ssl/ssl_stat.c
        ssl/ssl_txt.c
        ssl/ssl_utst.c
        ssl/statem/statem.c
        ssl/statem/statem_clnt.c
        ssl/statem/statem_dtls.c
        ssl/statem/extensions.c
        ssl/statem/extensions_clnt.c
        ssl/statem/extensions_srvr.c
        ssl/statem/extensions_cust.c
        ssl/statem/statem_lib.c
        ssl/statem/statem_srvr.c
        ssl/t1_enc.c
        ssl/t1_lib.c
        ssl/t1_trce.c
        ssl/tls_depr.c
        ssl/tls_srp.c
        ssl/tls13_enc.c
        )

PREPEND(ssl_srcs_with_path ${OPENSSL_PATH} ${ssl_srcs})
add_library(ssl ${SSLLIBTYPE} ${ssl_srcs_with_path})
target_compile_definitions(ssl PRIVATE -DOPENSSL_BUILDING_OPENSSL)


target_link_libraries(ssl crypto)

#MESSAGE(FATAL_ERROR "ASM is ${CMAKE_ASM_SOURCE_FILE_EXTENSIONS} and ${CMAKE_CXX_SOURCE_FILE_EXTENSIONS}")
