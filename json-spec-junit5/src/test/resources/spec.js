const s = require('@json-spec/core');
const gen = require('@json-spec/core/gen');
const sp = require('@json-spec/spec-profiles');
const sb = require('@json-spec/spec-basic');

/*
 Specification of a person object.
 */
const person = s.object({
  required: {
    firstName: sp.firstName({ size: 10, locale:"ja"}),
    lastName:  sp.lastName({ size: 10, locale: "ja" }),
    languages: s.array([
      "C", "C++", "Java"
    ], { distinct: true, maxCount: 3 })
  },
  optional: {
    birthDay:  sp.birthDay,
    postalCd:  sp.postalCode_JP
  }
});

const fullName = sp.name({ size: 4});

module.exports = {
  person,
  fullName
};